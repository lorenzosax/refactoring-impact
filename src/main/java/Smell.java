import com.opencsv.bean.CsvBindByPosition;

import java.io.Serializable;

public class Smell implements Serializable {

    @CsvBindByPosition(position = 0)
    private String projectName;
    @CsvBindByPosition(position = 1)
    private String packageName;
    @CsvBindByPosition(position = 2)
    private String className;
    @CsvBindByPosition(position = 3)
    private String methodName;
    @CsvBindByPosition(position = 4)
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
