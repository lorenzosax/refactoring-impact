import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefactoringMinerWorker {
    GitService gitService = new GitServiceImpl();
    GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
    Repository repo;

    public RefactoringMinerWorker(String pathToRepo) throws Exception {
        repo = gitService.openRepository(pathToRepo);
    }

    public RefactoringMinerWorker(String pathToDirectory, String repoUrl) throws Exception {
        repo = gitService.cloneIfNotExists(pathToDirectory, repoUrl);
    }

    public ArrayList<Commit> getCommitListRefactoringAffected() throws Exception {
        return getCommitListRefactoringAffected("main");
    }

    public ArrayList<Commit> getCommitListRefactoringAffected(String branch) throws Exception {
        ArrayList<Commit> commitArrayList = new ArrayList<>();
        miner.detectAll(repo, branch, new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                commitArrayList.add(new Commit(commitId, refactorings));
            }
        });
        return commitArrayList;
    }

    public boolean checkoutPreviousCommit(String commitHashId) throws Exception {

        ProcResult result = new ProcBuilder("git")
                .withWorkingDirectory(new File(repo.getDirectory().toString()))
                .withArg("rev-list")
                .withArg("--parents")
                .withArg("-n")
                .withArg("1")
                .withArg(commitHashId)
                .run();

        Pattern pattern = Pattern.compile(" (.*)");
        Matcher matcher = pattern.matcher(result.getOutputString().trim());
        String previousCommit;

        if(matcher.find()) {
            previousCommit = matcher.group(1);
            System.out.println("Checkout to commit id: " + previousCommit);
            gitService.checkout(repo, previousCommit);
            return true;
        }

        System.out.println("Match not found");
        return false;
    }
}