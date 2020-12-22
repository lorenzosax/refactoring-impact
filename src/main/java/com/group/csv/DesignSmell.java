package com.group.csv;

import com.opencsv.bean.CsvBindByPosition;

import java.io.Serializable;

public class DesignSmell implements Serializable {

    @CsvBindByPosition(position = 0)
    private String projectName;
    @CsvBindByPosition(position = 1)
    private String packageName;
    @CsvBindByPosition(position = 2)
    private String className;
    @CsvBindByPosition(position = 3)
    private String codeSmell;

    public DesignSmell() {}

    public DesignSmell(String projectName, String packageName, String className, String codeSmell) {
        this.projectName = projectName;
        this.packageName = packageName;
        this.className = className;
        this.codeSmell = codeSmell;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCodeSmell() {
        return codeSmell;
    }

    public void setCodeSmell(String codeSmell) {
        this.codeSmell = codeSmell;
    }
}
