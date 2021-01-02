package com.group;

import com.group.csv.CSVService;
import com.group.csv.ProcessResult;
import com.group.csv.Smell;
import com.group.pojo.Commit;
import com.group.pojo.InfoCommit;
import com.group.pojo.sonar.Analysis;
import com.group.worker.DesigniteWorker;
import com.group.worker.RefactoringMinerWorker;
import com.group.worker.SonarQubeWorker;
import org.apache.log4j.Logger;
import org.refactoringminer.api.Refactoring;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Process {

    static final String RESULTS_PROCESS_FILENAME = "datasets.csv";
    private static final Logger logger = Logger.getLogger(Process.class);
    private final String repoDir;
    private final String relativeSrcPath;
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
        relativeSrcPath = conf.getRelativeSrcPath();
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

    /**
     * Check if two given string contain filepath are the same
     *
     * @param refClassFilePath   refactoring class filepath
     * @param smellClassFilePath smell class filepath
     * @return true if the path are equals
     */
    private static boolean isSamePathClass(String refClassFilePath, String smellClassFilePath) {
        return refClassFilePath != null && refClassFilePath.equals(smellClassFilePath);
    }

    /**
     * Check if two method are the same
     *
     * @param refMethodName   refactoring method name
     * @param smellMethodName smell method name
     * @return a boolean value
     */
    private static boolean isSameMethod(String refMethodName, String smellMethodName) {
        if (refMethodName == null) return false;

        StringTokenizer st = new StringTokenizer(refMethodName, "(");
        String method = st.nextToken();

        StringTokenizer stSpace = new StringTokenizer(method, " ");
        while (stSpace.hasMoreElements())
            method = stSpace.nextToken();

        return method.equals(smellMethodName);
    }

    /**
     * Given the package string and the class string, generate smell class filepath.
     * If pkg contains packages with dot then replace with /.
     *
     * @param pkg       a String with packages
     * @param className a String with class name
     * @return a string contain the path of the class with packages
     */
    private static String generateSmellClassPath(String pkg, String className) {
        pkg = pkg.replace(".", "/");
        return pkg.contains(" ") ?
                className.concat(".java")
                : pkg.concat("/").concat(className).concat(".java");
    }

    /**
     * Start process to analyze a project and match code smells with refactoring
     * and associate class tech debts. Results will be write on CSV in the folder
     * specified in configuration file (application.conf)
     *
     * @see ProcessResult
     */
    public void start() throws Exception {

        RefactoringMinerWorker refactoringMinerWorker =
                new RefactoringMinerWorker(repoDir, resultsDir, writeRefactoringMinerOutputOnFile);
        DesigniteWorker designiteWorker = new DesigniteWorker(designiteDir, repoDir, resultsDir);
        SonarQubeWorker sonarQubeWorker = new SonarQubeWorker(sonarQubeServerBaseUrl, sonarQubeScannerBinDir,
                repoDir, relativeSrcPath);

        logger.info("<Start process>");

        ArrayList<Commit> commitList;
        if (refactoringMinerDetectBetweenCommits) {
            commitList = refactoringMinerWorker.getRefactoringsForCommitsWithRange(
                    refactoringMinerStartCommitId, refactoringMinerEndCommitId);
        } else {
            commitList = refactoringMinerWorker.getRefactoringsForCommits(branchName);
        }

        int commitNumber = 1, totalCommits = commitList.size();

        // Loop on all commits that contains refactorings
        for (Commit commit : commitList) {

            logger.info("Commit " + commitNumber++ + "/" + totalCommits + ")");

            String commitHashId = commit.getHash();
            String previousCommitHashId = refactoringMinerWorker.checkoutPreviousCommit(commitHashId);

            // if present previous commit do analysis
            if (previousCommitHashId != null) {

                // Get smell list of previous commit
                List<Smell> smellListPreviousCommit = designiteWorker.execute(previousCommitHashId);

                // if designite return at least 1 smell do analysis
                if (smellListPreviousCommit.size() > 0) {

                    refactoringMinerWorker.checkoutToCommit(commitHashId);
                    InfoCommit infoCommit = refactoringMinerWorker.getInformationCommit(commitHashId);

                    // Get smell list of current commit
                    List<Smell> smellListActualCommit = designiteWorker.execute(commitHashId);

                    boolean sonarQubeScanningAlreadyDone = false;
                    Integer tdDiff = null;
                    Analysis actualAnalysis = null, previousAnalysis = null;
                    ProcessResult pr;
                    boolean refactoringsRemoveSmell;

                    // for each previous smell considerate all refactoring of current commit
                    for (Smell s0 : smellListPreviousCommit) {
                        refactoringsRemoveSmell = false;

                        String smellClassPath = generateSmellClassPath(s0.getPackageName(), s0.getClassName());
                        for (Refactoring r : commit.getRefactoringList()) {

                            // if smell and refactorings have to do between them (same class and, if exists, same method) then create entry regardless of true or false
                            boolean isAEntry = r.leftSide() != null && r.leftSide().size() > 0
                                    && isSamePathClass(Utils.getPackagesWithClassPath(r.leftSide().get(0).getFilePath()), smellClassPath)
                                    && (s0.getMethodName() == null || isSameMethod(r.leftSide().get(0).getCodeElement(), s0.getMethodName()));

                            if (isAEntry) {
                                // Prepare new entry for results
                                pr = new ProcessResult();
                                pr.setCommitHash(commitHashId);
                                pr.setClassName(s0.getClassName());
                                pr.setMethodName(s0.getMethodName() == null ? "-" : s0.getMethodName());
                                pr.setCommitterName(infoCommit.getAuthor());
                                pr.setCommitterEmail(infoCommit.getEmail());
                                pr.setSmellType(s0.getCodeSmell());
                                pr.setRefactoringType(r.getRefactoringType().getDisplayName());

                                // to avoid throwing multiple times SonarQubeScanner check
                                // if already done for current and previous commit
                                if (!sonarQubeScanningAlreadyDone) {
                                    sonarQubeWorker.executeScanning(commitHashId);
                                    refactoringMinerWorker.checkoutToCommit(previousCommitHashId);
                                    sonarQubeWorker.executeScanning(previousCommitHashId);

                                    // retrieve analysis from SonarQube Server
                                    actualAnalysis = sonarQubeWorker.getAnalysisFor(commitHashId);
                                    previousAnalysis = sonarQubeWorker.getAnalysisFor(previousCommitHashId);
                                    sonarQubeScanningAlreadyDone = true;
                                }

                                tdDiff = sonarQubeWorker.extractTdFromComponent(previousAnalysis, smellClassPath)
                                        - sonarQubeWorker.extractTdFromComponent(actualAnalysis, smellClassPath);

                                pr.setTdDifference(tdDiff);
                                pr.setTdClass(ProcessResult.getTdClassFor(tdDiff));

                                // We suppose that it was been refactoring, because smell is not in the current list, so removed
                                if (!smellListActualCommit.contains(s0)) {
                                    // since the smell s0 has been removed from current refactoring (regardless other refactoring), it's useless to check for other cause after
                                    refactoringsRemoveSmell = true;
                                    pr.setSmellRemovedWithRefactoring(true);
                                }

                                // the smell is still present, despite there have been refactoring
                                else
                                    pr.setSmellRemovedWithRefactoring(false);

                                // obviously it has not been remove and so neither for other causes
                                pr.setSmellRemovedWithoutRefactoring(false);

                                resultList.add(pr);
                            }
                        }
                        // Check if the smell has not been removed due to at least one previous refactoring (refactoringRemoveSmell)
                        // Smell may have been fixed for other causes and so if the smell is no longer in the current list, then there were other causes
                        if (!refactoringsRemoveSmell && !smellListActualCommit.contains(s0)) {
                            pr = new ProcessResult();
                            pr.setCommitHash(commitHashId);
                            pr.setClassName(s0.getClassName());
                            pr.setMethodName(s0.getMethodName() == null ? "-" : s0.getMethodName());
                            pr.setCommitterName(infoCommit.getAuthor());
                            pr.setCommitterEmail(infoCommit.getEmail());
                            pr.setSmellType(s0.getCodeSmell());
                            // obviously it was not refactoring
                            pr.setSmellRemovedWithRefactoring(false);

                            // to avoid throwing multiple times SonarQubeScanner check
                            // if already done for current and previous commit
                            if (!sonarQubeScanningAlreadyDone) {
                                sonarQubeWorker.executeScanning(commitHashId);
                                refactoringMinerWorker.checkoutToCommit(previousCommitHashId);
                                sonarQubeWorker.executeScanning(previousCommitHashId);

                                actualAnalysis = sonarQubeWorker.getAnalysisFor(commitHashId);
                                previousAnalysis = sonarQubeWorker.getAnalysisFor(previousCommitHashId);
                                sonarQubeScanningAlreadyDone = true;
                            }

                            tdDiff = sonarQubeWorker.extractTdFromComponent(previousAnalysis, smellClassPath)
                                    - sonarQubeWorker.extractTdFromComponent(actualAnalysis, smellClassPath);

                            pr.setTdDifference(tdDiff);
                            pr.setTdClass(ProcessResult.getTdClassFor(tdDiff));

                            pr.setSmellRemovedWithoutRefactoring((true));

                            resultList.add(pr);
                        }
                    }
                }
            }
            logger.info("-----------------------------------------");

            // Save result list in csv file each 5 commit analyzed
            if (commitNumber % 5 == 0) {
                logger.info("Updating " + RESULTS_PROCESS_FILENAME);
                CSVService.writeCsvFileWithStrategy(Utils.preparePathOsBased(false, resultsDir, RESULTS_PROCESS_FILENAME), resultList, ProcessResult.class, false, true);
                resultList.clear();
            }
        }
        logger.info("Updating " + RESULTS_PROCESS_FILENAME);
        CSVService.writeCsvFileWithStrategy(Utils.preparePathOsBased(false, resultsDir, RESULTS_PROCESS_FILENAME), resultList, ProcessResult.class, false, true);
        logger.info("Process finished!");
    }

}
