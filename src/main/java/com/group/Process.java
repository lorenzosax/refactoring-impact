package com.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;

import com.group.csv.CSVService;
import com.group.csv.Smell;
import com.group.pojo.Commit;
import com.group.pojo.InfoCommit;
import com.group.csv.ProcessResult;
import com.group.pojo.sonar.Analysis;
import com.group.worker.DesigniteWorker;
import com.group.worker.RefactoringMinerWorker;
import com.group.worker.SonarQubeWorker;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import org.apache.log4j.Logger;

public class Process {

    private static final Logger logger = Logger.getLogger(Process.class);

    static final String RESULTS_PROCESS_FILENAME = "datasets.csv";

    public Process() {
    }

    public void start() throws Exception {

        // region Load Configurations
        Configuration conf = Configuration.getInstance();
        String repoDir = conf.getRepoDir();
        String branchName = conf.getRefactoringMinerBranchToAnalyze();
        boolean refactoringMinerDetectBetweenCommits = conf.isRefactoringMinerDetectBetweenCommits();
        String refactoringMinerStartCommitId = conf.getRefactoringMinerStartCommitId();
        String refactoringMinerEndCommitId = conf.getRefactoringMinerEndCommitId();
        boolean writeRefactoringMinerOutputOnFile = conf.isWriteRefactoringMinerOutputOnFile();
        String designiteDir = conf.getDesigniteDir();
        String sonarQubeServerBaseUrl = conf.getSonarQubeServerBaseUrl();
        String sonarQubeScannerBinDir = conf.getSonarQubeScannerBinDir();
        String resultsDir = conf.getResultsDir();
        // endregion

        List<ProcessResult> resultList = new ArrayList<>();

        RefactoringMinerWorker refactoringMinerWorker =
                new RefactoringMinerWorker(repoDir, resultsDir, writeRefactoringMinerOutputOnFile);
        DesigniteWorker designiteWorker = new DesigniteWorker(designiteDir, repoDir, resultsDir);
        SonarQubeWorker sonarQubeWorker = new SonarQubeWorker(sonarQubeServerBaseUrl, sonarQubeScannerBinDir,repoDir);

        logger.info("<Start process>");

        ArrayList<Commit> commitList;
        if (refactoringMinerDetectBetweenCommits) {
            commitList = refactoringMinerWorker.getRefactoringsForCommitsWithRange(
                    refactoringMinerStartCommitId, refactoringMinerEndCommitId);
        } else {
            commitList = refactoringMinerWorker.getRefactoringsForCommits(branchName);
        }

        for (Commit commit : commitList) {

            String commitHashId = commit.getHash();
            String previousCommitHashId = refactoringMinerWorker.checkoutPreviousCommit(commitHashId);

            if (previousCommitHashId != null) {

                List<Smell> smellListPreviousCommit = designiteWorker.execute(previousCommitHashId);

                if (smellListPreviousCommit.size() > 0) {

                    refactoringMinerWorker.checkoutToCommit(commitHashId);
                    InfoCommit infoCommit = refactoringMinerWorker.getInformationCommit(commitHashId);
                    List<Smell> smellListActualCommit = designiteWorker.execute(commitHashId);

                    boolean sonarQubeScanningAlreadyDone = false;
                    Integer tdDiff;
                    Analysis actualAnalysis = null, previousAnalysis = null;
                    for (Smell s0 : smellListPreviousCommit) {
                        for (Refactoring r : commit.getRefactoringList()) {

                            ProcessResult pr = new ProcessResult();
                            pr.setCommitHash(commitHashId);
                            pr.setClassName(s0.getClassName());
                            pr.setMethodName(s0.getMethodName() == null ? "-" : s0.getMethodName());
                            pr.setCommitterName(infoCommit.getAuthor());
                            pr.setCommitterEmail(infoCommit.getEmail());
                            pr.setSmellType(s0.getCodeSmell());
                            pr.setSmellRemoved(!smellListActualCommit.contains(s0));
                            pr.setRefactoringType(r.getRefactoringType().getDisplayName());

                            if (!sonarQubeScanningAlreadyDone) {
                                sonarQubeWorker.executeScanning(commitHashId);
                                refactoringMinerWorker.checkoutToCommit(previousCommitHashId);
                                sonarQubeWorker.executeScanning(previousCommitHashId);

                                actualAnalysis = sonarQubeWorker.getAnalysisFor(commitHashId);
                                previousAnalysis = sonarQubeWorker.getAnalysisFor(previousCommitHashId);
                                sonarQubeScanningAlreadyDone = true;
                            }

                            tdDiff = sonarQubeWorker.extractTdFromComponent(previousAnalysis, r.leftSide().get(0).getFilePath())
                                    - sonarQubeWorker.extractTdFromComponent(actualAnalysis, r.leftSide().get(0).getFilePath());

                            pr.setTdDifference(tdDiff);
                            pr.setTdClass(ProcessResult.getTdClassFor(tdDiff));

                            boolean isSmellRemoved = r.leftSide() != null && r.leftSide().size() > 0
                                    && !smellListActualCommit.contains(s0)
                                    && isSamePathClass(getPackagesWithClassPath(r.leftSide().get(0).getFilePath()), generateSmellClassPath(s0.getPackageName(), s0.getClassName()))
                                    && (s0.getMethodName() == null || isSameMethod(r.leftSide().get(0).getCodeElement(), s0.getMethodName()));

                            pr.setSmellRemoved(isSmellRemoved);

                            resultList.add(pr);
                        }
                    }
                }
            }
            logger.info("-----------------------------------------");
        }
        logger.info("Generating " + RESULTS_PROCESS_FILENAME);
        CSVService.writeCsvFile(resultsDir + "\\" + RESULTS_PROCESS_FILENAME, resultList, ProcessResult.class);
        logger.info("Process finished!");
    }

    public static boolean isAdmissibleRefactoringType(RefactoringType refType, String codeSmell) {
        Map<RefactoringType, Boolean> ref = Utils.allowedSmellWithRefactoringTypes.get(codeSmell);
        return ref.containsKey(refType);
    }

    public static boolean isSamePathClass(String refClassFilePath, String smellClassFilePath) {
        return refClassFilePath.equals(smellClassFilePath);
    }

    public static boolean isSameMethod(String refMethodName, String smellMethodName) {
        StringTokenizer st = new StringTokenizer(refMethodName, "(");
        String method = st.nextToken();

        StringTokenizer stSpace = new StringTokenizer(method, " ");
        while (stSpace.hasMoreElements())
            method = stSpace.nextToken();

        return method.equals(smellMethodName);
    }

    public static String getPackagesWithClassPath(String filepath) {

        Matcher srcMainJavaMatcher = Utils.srcMainJavaPattern.matcher(filepath);
        Matcher scrMatcher = Utils.srcPattern.matcher(filepath);

        if(srcMainJavaMatcher.find()) {
            return srcMainJavaMatcher.group(2);
        } else if (scrMatcher.find()){
            return scrMatcher.group(1);
        }
        return filepath;
    }

    public static String generateSmellClassPath(String pkg, String className) {
        pkg = pkg.replace(".", "/");
        return pkg.contains(" ") ?
                className.concat(".java")
                : pkg.concat("/").concat(className).concat(".java");
    }
}
