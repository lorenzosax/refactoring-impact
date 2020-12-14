import org.refactoringminer.api.Refactoring;

import java.util.List;

public class Commit {
    private String hash;
    private List<Refactoring> refactoringList;

    public Commit(String hash, List<Refactoring> refactoringList) {
        this.hash = hash;
        this.refactoringList = refactoringList;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<Refactoring> getRefactoringList() {
        return refactoringList;
    }

    public void setRefactoringList(List<Refactoring> refactoringList) {
        this.refactoringList = refactoringList;
    }
}
