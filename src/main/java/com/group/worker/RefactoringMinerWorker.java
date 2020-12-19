package com.group.worker;

import com.group.pojo.InfoCommit;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import com.group.pojo.Commit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefactoringMinerWorker {

    GitService gitService = new GitServiceImpl();
    GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
    Repository repo;
    String resultsDir;

    public RefactoringMinerWorker(String repoDir, String resultsDir) throws Exception {
        repo = gitService.openRepository(repoDir);
        this.resultsDir = resultsDir;
    }

    public RefactoringMinerWorker(String pathToDirectory, String repoUrl, String resultsDir) throws Exception {
        repo = gitService.cloneIfNotExists(pathToDirectory, repoUrl);
        this.resultsDir = resultsDir;
    }

    public ArrayList<Commit> getCommitListRefactoringAffected() throws Exception {
        return getCommitListRefactoringAffected("main");
    }

    public ArrayList<Commit> getCommitListRefactoringAffected(String branch) throws Exception {
        System.out.println("Run Refactoring Miner...");
        ArrayList<Commit> commitArrayList = new ArrayList<>();
        miner.detectAll(repo, branch, new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                if (refactorings.size() > 0) {
                    commitArrayList.add(new Commit(commitId, refactorings));
                }
            }
        });
        System.out.println("Refactoring Miner Done!");
        return commitArrayList;
    }

    public String checkoutPreviousCommit(String commitHashId) {

        System.out.println("Commit Hash: " + commitHashId);

        ProcResult result = new ProcBuilder("git")
                .withWorkingDirectory(new File(repo.getDirectory().getParent()))
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
            checkoutToCommit(previousCommit);
            return previousCommit;
        }

        System.out.println("Previous Commit hash not found: " + commitHashId);
        return null;
    }

    public boolean checkoutToCommit(String commitHashId) {
        ProcResult procResult = new ProcBuilder("git")
                .withWorkingDirectory(new File(repo.getDirectory().getParent()))
                .withArg("checkout")
                .withArg("-f")
                .withArg(commitHashId)
                .run();

        System.out.println("Checkout to commit hash: " + commitHashId);
        return true;
    }

    public InfoCommit getInformationCommit(String commitHashId) {
        ProcResult procResult = new ProcBuilder("git")
                .withWorkingDirectory(new File(repo.getDirectory().getParent()))
                .withArg("log")
                .withArg("--pretty=format:\"Commit:%n%H%n%cn%n%ce\"")
                .withArg(commitHashId)
                .run();


        return parseInfoCommit(procResult);
    }

    private static InfoCommit parseInfoCommit(ProcResult procResult) {
        InfoCommit infoCommit = null;

        String[] results =  procResult.getOutputString() != null ? procResult.getOutputString().split("\n") : null;
        if (results != null && results.length > 3) {
            infoCommit = new InfoCommit(results[1], results[2], results[3]);
        }

        return infoCommit;
    }
}