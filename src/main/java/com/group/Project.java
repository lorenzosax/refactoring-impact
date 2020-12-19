package com.group;

import com.group.csv.CSVService;
import com.group.csv.Smell;
import com.group.pojo.Commit;
import com.group.pojo.InfoCommit;
import com.group.pojo.ProcessResult;
import com.group.worker.DesigniteWorker;
import com.group.worker.RefactoringMinerWorker;

import java.util.*;

public class Project {

    public static void main(String[] args) throws Exception {

        String repo = "C:\\Users\\loren\\IdeaProjects\\minic";
        String designiteDir = "C:\\Users\\loren\\IdeaProjects\\TOOL";
        String sonarQubeServerBinDir = "C:\\Users\\loren\\IdeaProjects\\sonarqube-8.5.1.38104\\bin\\windows-x86-64";
        String sonarQubeScannerBinDir = "C:\\Users\\loren\\IdeaProjects\\sonar-scanner-4.5.0.2216-windows\\bin";

        List<ProcessResult> resultList = new ArrayList<>();

        RefactoringMinerWorker refactoringMinerWorker = new RefactoringMinerWorker(repo);
        DesigniteWorker designiteWorker = new DesigniteWorker(designiteDir, repo);

        System.out.println("<Start process>");
        System.out.println("-----------------------------------------");
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

                    if (smellListActualCommit.size() > 0) {
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
            }
            System.out.println("-----------------------------------------");
        }
        CSVService.writeCsvFile("result.csv", resultList, ProcessResult.class);
        System.out.println("Process finished!");
    }
}
