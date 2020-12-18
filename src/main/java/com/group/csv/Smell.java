package com.group.csv;

import com.opencsv.bean.CsvBindByName;
import com.group.csv.annotation.CsvBindByNameOrder;

import java.io.Serializable;

@CsvBindByNameOrder({"Project","Package","Class","Method","Code Smell"})
public class Smell implements Serializable {
    
    @CsvBindByName(column = "Project")
    private String projectName;
    @CsvBindByName(column = "Package")
    private String packageName;
    @CsvBindByName(column = "Class")
    private String className;
    @CsvBindByName(column = "Method")
    private String methodName;
    @CsvBindByName(column = "Code Smell")
    private String codeSmell;

    public Smell(String projectName, String packageName, String className, String methodName, String codeSmell) {
        this.projectName = projectName;
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.codeSmell = codeSmell;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getCodeSmell() {
        return codeSmell;
    }
}
