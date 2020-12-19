package com.group.csv;

import com.group.csv.annotation.CsvBindByNameOrder;
import com.opencsv.bean.CsvBindByName;

@CsvBindByNameOrder({"Commit","Refactoring Type","Description"})
public class RefactoringCSV {

    @CsvBindByName(column = "Commit")
    private String commit;
    @CsvBindByName(column = "Refactoring Type")
    private String refactoringType;
    @CsvBindByName(column = "Description")
    private String description;

    public RefactoringCSV(String commit, String refactoringType, String description) {
        this.commit = commit;
        this.refactoringType = refactoringType;
        this.description = description;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getRefactoringType() {
        return refactoringType;
    }

    public void setRefactoringType(String refactoringType) {
        this.refactoringType = refactoringType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
