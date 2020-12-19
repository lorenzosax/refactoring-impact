package com.group.worker;

import com.group.csv.CSVDesignSmell;
import com.group.csv.CSVImplementationSmell;
import com.group.csv.CSVService;
import com.group.csv.Smell;
import org.buildobjects.process.ProcBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesigniteWorker {

    private static final String DESIGNITE_RESULTS_FOLDER = "designiteResults";
    private static final String DESIGN_CODE_SMELLS_FILENAME = "designCodeSmells.csv";
    private static final String IMPLEMENTATION_CODE_SMELLS_FILENAME = "implementationCodeSmells.csv";
    private static final String FILTERED_SMELLS_FILENAME = "filteredSmells.csv";

    static final Map<String, Boolean> ALLOWED_SMELL = new HashMap<String, Boolean>() {{
       put("Long Method", true);
       put("Long Parameter List", true);
       put("Insufficient Modularization", true);
       put("Multifaceted Abstraction", true);
    }};

    private String designitePath;
    private String projectDir;

    public DesigniteWorker(String designitePath, String projectDir){
        this.designitePath = designitePath;
        this.projectDir = projectDir;
        new ProcBuilder("mkdir")
                .withArg(DESIGNITE_RESULTS_FOLDER);
    }

    private String getResultsPath() {
        return designitePath + "\\" + DESIGNITE_RESULTS_FOLDER + "\\";
    }

    public List<Smell> execute(String folderName) {

        System.out.println("Run Designite...");

        String outputPath = getResultsPath() + System.currentTimeMillis() + "_" + folderName + "\\";

        new ProcBuilder("java")
                .withWorkingDirectory(new File(designitePath))
                .withArg("-jar")
                .withArg("DesigniteJava.jar")
                .withArg("-i")
                .withArg(projectDir)
                .withArg("-o")
                .withArg(outputPath)
                .run();
        System.out.println("Designite Done!");

        System.out.println("Read " + DESIGN_CODE_SMELLS_FILENAME);
        List<CSVDesignSmell> designSmellList = CSVService.readCsvFile(
                outputPath + DESIGN_CODE_SMELLS_FILENAME, CSVDesignSmell.class);
        System.out.println("Read " + IMPLEMENTATION_CODE_SMELLS_FILENAME);
        List<CSVImplementationSmell> implementationSmellList = CSVService.readCsvFile(
                outputPath + IMPLEMENTATION_CODE_SMELLS_FILENAME,
                CSVImplementationSmell.class);

        List<Smell> smellList = new ArrayList<>();
        for(CSVDesignSmell ds : designSmellList) {
            if (ALLOWED_SMELL.containsKey(ds.getCodeSmell())) {
                smellList.add(
                        new Smell(
                                ds.getProjectName(),
                                ds.getPackageName(),
                                ds.getClassName(),
                                null,
                                ds.getCodeSmell())
                );
            }
        }

        for(CSVImplementationSmell is : implementationSmellList) {
            if (ALLOWED_SMELL.containsKey(is.getCodeSmell())) {
                smellList.add(
                        new Smell(
                                is.getProjectName(),
                                is.getPackageName(),
                                is.getClassName(),
                                is.getMethodName(),
                                is.getCodeSmell())
                );
            }
        }

        System.out.println("Generating " + FILTERED_SMELLS_FILENAME);
        CSVService.writeCsvFileWithStrategy(outputPath + FILTERED_SMELLS_FILENAME, smellList, Smell.class);

        return smellList;
    }

}
