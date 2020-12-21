package com.group.csv;

import com.group.csv.annotation.CsvBindByNameOrder;
import com.opencsv.bean.CsvBindByName;

@CsvBindByNameOrder({"Commit","Committer Name","Committer Email","Class","Method","Refactoring Type","Smell Type","TD difference","TD Class","Smell Removed})
public class ProcessResult {

    public enum TD_CLASS { IMPROVED, STABLE, PEJORATIVE};

    @CsvBindByName(column = "Commit")
    private String commitHash;
    @CsvBindByName(column = "Committer Name")
    private String committerName;
    @CsvBindByName(column = "Committer Email")
    private String committerEmail;
    @CsvBindByName(column = "Class")
    private String className;
    @CsvBindByName(column = "Method")
    private String methodName;
    @CsvBindByName(column = "Refactoring Type")
    private String refactoringType;
    @CsvBindByName(column = "Smell Type")
    private String smellType;
    @CsvBindByName(column = "TD difference")
    private Integer tdDifference;
    @CsvBindByName(column = "TD Class")
    private TD_CLASS tdClass;
    @CsvBindByName(column = "Smell Removed")
    private boolean isSmellRemoved;

    public ProcessResult() {
        this.tdDifference = 0;
    }

    public ProcessResult(String commitHash, String committerName, String committerEmail, String className,
                         String methodName, String refactoringType, String smellType, Integer tdDifference,
                         TD_CLASS tdClass, boolean isSmellRemoved) {
        this.commitHash = commitHash;
        this.committerName = committerName;
        this.committerEmail = committerEmail;
        this.className = className;
        this.methodName = methodName;
        this.refactoringType = refactoringType;
        this.smellType = smellType;
        this.tdDifference = tdDifference;
        this.tdClass = tdClass;
        this.isSmellRemoved = isSmellRemoved;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getCommitterName() {
        return committerName;
    }

    public void setCommitterName(String committerName) {
        this.committerName = committerName;
    }

    public String getCommitterEmail() {
        return committerEmail;
    }

    public void setCommitterEmail(String committerEmail) {
        this.committerEmail = committerEmail;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getRefactoringType() {
        return refactoringType;
    }

    public void setRefactoringType(String refactoringType) {
        this.refactoringType = refactoringType;
    }

    public String getSmellType() {
        return smellType;
    }

    public void setSmellType(String smellType) {
        this.smellType = smellType;
    }

    public Integer getTdDifference() {
        return tdDifference;
    }

    public void setTdDifference(Integer tdDifference) {
        this.tdDifference = tdDifference;
    }

    public TD_CLASS getTdClass() {
        return tdClass;
    }

    public void setTdClass(TD_CLASS tdClass) {
        this.tdClass = tdClass;
    }

    public boolean isSmellRemoved() {
        return isSmellRemoved;
    }

    public void setSmellRemoved(boolean smellRemoved) {
        isSmellRemoved = smellRemoved;
    }

    public static TD_CLASS getTdClassFor(Integer value) {
        if (value > 0)
            return TD_CLASS.PEJORATIVE;
        else if (value < 0)
            return TD_CLASS.IMPROVED;
        else
            return TD_CLASS.STABLE;
    }
}
