import java.util.ArrayList;

public class Project {
    public static void main(String[] args) throws Exception {
        String repo = "C:\\Users\\loren\\IdeaProjects\\minic";
        RefactoringMinerWorker refactoringMinerWorker = new RefactoringMinerWorker(repo);

        ArrayList<Commit> commitList = refactoringMinerWorker.getCommitListRefactoringAffected();

        // Only for test
        Commit c = commitList.get(0);
        refactoringMinerWorker.checkoutPreviousCommit(c.getHash());

        /*
        for (Commit commit : commitList) {
            refactoringMinerWorker.checkoutPreviousCommit(commit.getHash());
            // TODO Run designite ...
        }
        */


    }
}
