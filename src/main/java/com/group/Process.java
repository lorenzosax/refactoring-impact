package com.group;

import com.group.csv.CSVService;
import com.group.csv.Smell;
import com.group.pojo.Commit;
import com.group.pojo.InfoCommit;
import com.group.pojo.ProcessResult;
import com.group.worker.DesigniteWorker;
import com.group.worker.RefactoringMinerWorker;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.*;

public class Process {

    static final String RESULTS_PROCESS_FILENAME = "datasets.csv";

    public static void main(String[] args) throws Exception {

        Config conf = ConfigFactory.load();
        String repoDir = conf.getString("repo.dir");
        String designiteDir = conf.getString("designite.dir");
        String sonarQubeServerBinDir = conf.getString("sonarqube.server.bin-dir");
        String sonarQubeScannerBinDir = conf.getString("sonarqube.scanner.bin-dir");
        String resultsDir = conf.getString("results.dir");

        List<ProcessResult> resultList = new ArrayList<>();

        RefactoringMinerWorker refactoringMinerWorker = new RefactoringMinerWorker(repoDir, resultsDir);
        DesigniteWorker designiteWorker = new DesigniteWorker(designiteDir, repoDir, resultsDir);

        System.out.println("<Start process>");

        ArrayList<Commit> commitList = refactoringMinerWorker.getCommitListRefactoringAffected();

        for (Commit commit : commitList) {

            String commitHashId = commit.getHash();
            String previousCommitHashId = refactoringMinerWorker.checkoutPreviousCommit(commitHashId);

            if (previousCommitHashId != null) {

                List<Smell> smellListPreviousCommit = designiteWorker.execute(previousCommitHashId);

                if (smellListPreviousCommit.size() > 0) {

                    refactoringMinerWorker.checkoutToCommit(commitHashId);
                    InfoCommit infoCommit = refactoringMinerWorker.getInformationCommit(commitHashId);
                    List<Smell> smellListActualCommit = designiteWorker.execute(commitHashId);

                    for (Smell s0 : smellListPreviousCommit) {
                        ProcessResult pr = new ProcessResult();
                        pr.setCommitHash(commitHashId);
                        pr.setClassName(s0.getClassName());
                        pr.setMethodName(s0.getMethodName());
                        pr.setCommitterName(infoCommit.getAuthor());
                        pr.setCommitterEmail(infoCommit.getEmail());
                        pr.setSmellType(s0.getCodeSmell());
                        if (smellListActualCommit.contains(s0)) {
                            pr.setSmellRemoved(false);
                        } else {
                            pr.setSmellRemoved(true);
                            // lo smell s0 è stato risolto: indagare quale refactoring di riferimento
                            // sonarscanner
                        }
                        resultList.add(pr);
                    }
                }
            }
            System.out.println("-----------------------------------------");
        }
        System.out.println("Generating " + RESULTS_PROCESS_FILENAME);
        CSVService.writeCsvFile(resultsDir + "\\" + RESULTS_PROCESS_FILENAME, resultList, ProcessResult.class);
        System.out.println("Process finished!");
    }
}
