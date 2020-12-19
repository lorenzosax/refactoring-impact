package com.group.pojo;

public class ProcessResult {

    private String commitHash;
    private String committerName;
    private String committerEmail;
    private String className;
    private String methodName;
    private String refactoringType;
    private String smellType;
    private Integer tdDifference;
    private String tdClass;
    private boolean isSmellRemoved;

    public ProcessResult() {
        this.tdDifference = 0;
    }

    public ProcessResult(String commitHash, String committerName, String committerEmail, String className,
                         String methodName, String refactoringType, String smellType, Integer tdDifference,
                         String tdClass, boolean isSmellRemoved) {
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

    public String getTdClass() {
        return tdClass;
    }

    public void setTdClass(String tdClass) {
        this.tdClass = tdClass;
    }

    public boolean isSmellRemoved() {
        return isSmellRemoved;
    }

    public void setSmellRemoved(boolean smellRemoved) {
        isSmellRemoved = smellRemoved;
    }
}
