package com.group.pojo;

public class InfoCommit {

    private String commitHash;
    private String author;
    private String email;

    public InfoCommit(String commitHash, String author, String email) {
        this.commitHash = commitHash;
        this.author = author;
        this.email = email;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
