package com.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

    private final String repoDir;
    private final String branchName;
    private final boolean refactoringMinerDetectBetweenCommits;
    private final String refactoringMinerStartCommitId;
    private final String refactoringMinerEndCommitId;
    private final boolean writeRefactoringMinerOutputOnFile;
    private final String designiteDir;
    private final String sonarQubeServerBaseUrl;
    private final String sonarQubeScannerBinDir;
    private final String resultsDir;
    private List<ProcessResult> resultList;

    public Process() {
        Configuration conf = Configuration.getInstance();
        repoDir = conf.getRepoDir();
        branchName = conf.getRefactoringMinerBranchToAnalyze();
        refactoringMinerDetectBetweenCommits = conf.isRefactoringMinerDetectBetweenCommits();
        refactoringMinerStartCommitId = conf.getRefactoringMinerStartCommitId();
        refactoringMinerEndCommitId = conf.getRefactoringMinerEndCommitId();
        writeRefactoringMinerOutputOnFile = conf.isWriteRefactoringMinerOutputOnFile();
        designiteDir = conf.getDesigniteDir();
        sonarQubeServerBaseUrl = conf.getSonarQubeServerBaseUrl();
        sonarQubeScannerBinDir = conf.getSonarQubeScannerBinDir();
        resultsDir = conf.getResultsDir();

        resultList = new ArrayList<>();
        CSVService.writeCsvFileWithStrategy(
                Utils.preparePathOsBased(false, resultsDir, RESULTS_PROCESS_FILENAME),
                resultList,
                ProcessResult.class,
                true,
                false);
    }

    public void start() throws Exception {

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

        int commitNumber = 1;
        for (Commit commit : commitList) {

            logger.info("Commit " + commitNumber++ + ")");

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

                            String smellClassPath = generateSmellClassPath(s0.getPackageName(), s0.getClassName());
                            tdDiff = sonarQubeWorker.extractTdFromComponent(previousAnalysis, smellClassPath)
                                    - sonarQubeWorker.extractTdFromComponent(actualAnalysis, smellClassPath);

                            pr.setTdDifference(tdDiff);
                            pr.setTdClass(ProcessResult.getTdClassFor(tdDiff));

                            boolean isSmellRemoved = r.leftSide() != null && r.leftSide().size() > 0
                                    && !smellListActualCommit.contains(s0)
                                    && isSamePathClass(Utils.getPackagesWithClassPath(r.leftSide().get(0).getFilePath()), smellClassPath)
                                    && (s0.getMethodName() == null || isSameMethod(r.leftSide().get(0).getCodeElement(), s0.getMethodName()));

                            pr.setSmellRemoved(isSmellRemoved);

                            resultList.add(pr);
                        }
                    }
                }
            }
            logger.info("-----------------------------------------");
            if (commitNumber % 5 == 0) {
                logger.info("Updating " + RESULTS_PROCESS_FILENAME);
                CSVService.writeCsvFileWithStrategy(Utils.preparePathOsBased(false, resultsDir, RESULTS_PROCESS_FILENAME), resultList, ProcessResult.class,false, true);
                resultList.clear();
            }
        }
        logger.info("Updating " + RESULTS_PROCESS_FILENAME);
        CSVService.writeCsvFileWithStrategy(Utils.preparePathOsBased(false, resultsDir, RESULTS_PROCESS_FILENAME), resultList, ProcessResult.class, false, true);
        logger.info("Process finished!");
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

    public static String generateSmellClassPath(String pkg, String className) {
        pkg = pkg.replace(".", "/");
        return pkg.contains(" ") ?
                className.concat(".java")
                : pkg.concat("/").concat(className).concat(".java");
    }
}
