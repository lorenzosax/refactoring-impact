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

    @Override
    public boolean equals(Object obj) {

        if ( (this.methodName == null && ((Smell)obj).getMethodName() != null) ||
                (this.methodName != null && ((Smell)obj).getMethodName() == null))
            return false;

        return this.projectName.equals(((Smell)obj).getProjectName()) &&
                this.packageName.equals(((Smell)obj).getPackageName()) &&
                this.className.equals(((Smell)obj).getClassName()) &&
                (
                    (this.methodName == null && ((Smell)obj).getMethodName() == null) ||
                        this.methodName.equals(((Smell)obj).getMethodName())
                ) &&
                this.codeSmell.equals(((Smell)obj).getCodeSmell());
    }
}
