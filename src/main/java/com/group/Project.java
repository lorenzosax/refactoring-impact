package com.group;

import com.group.csv.Smell;
import com.group.pojo.Commit;
import com.group.worker.DesigniteWorker;
import com.group.worker.RefactoringMinerWorker;

import java.util.*;

public class Project {

    public static void main(String[] args) throws Exception {

        String repo = "C:\\Users\\loren\\IdeaProjects\\minic";
        String designite = "C:\\Users\\loren\\IdeaProjects\\TOOL";
        RefactoringMinerWorker refactoringMinerWorker = new RefactoringMinerWorker(repo);
        DesigniteWorker designiteWorker = new DesigniteWorker(designite, repo);

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
                    List<Smell> smellListActualCommit = designiteWorker.execute(commitHashId);

                    if (smellListActualCommit.size() > 0) {
                        for (Smell s0 : smellListPreviousCommit) {
                            if (smellListActualCommit.contains(s0)) {
                                // lo smell s0 non è stato risolto con il commit in questione
                            } else {
                                // lo smell s0 è stato risolto indagare quale refactoring di riferimento
                            }
                        }
                    }
                }
            }
            System.out.println("-----------------------------------------");
        }
        System.out.println("Process finished!");
    }
}
