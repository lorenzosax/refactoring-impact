package com.group.worker;

import com.group.Utils;
import com.group.csv.DesignSmell;
import com.group.csv.ImplementationSmell;
import com.group.csv.CSVService;
import com.group.csv.Smell;
import org.buildobjects.process.ProcBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DesigniteWorker {

    private static final String DESIGNITE_RESULTS_FOLDER = "designiteResults";
    private static final String DESIGN_CODE_SMELLS_FILENAME = "designCodeSmells.csv";
    private static final String IMPLEMENTATION_CODE_SMELLS_FILENAME = "implementationCodeSmells.csv";
    private static final String FILTERED_SMELLS_FILENAME = "filteredSmells.csv";

    private String designiteDir;
    private String repoDir;
    private String resultsDir;

    public DesigniteWorker(String designiteDir, String repoDir, String resultsDir){
        this.designiteDir = designiteDir;
        this.repoDir = repoDir;
        new ProcBuilder("mkdir")
                .withWorkingDirectory(new File(resultsDir))
                .withArg(DESIGNITE_RESULTS_FOLDER)
                .run();
        this.resultsDir = resultsDir + "\\" + DESIGNITE_RESULTS_FOLDER + "\\";
    }

    public List<Smell> execute(String folderName) {

        System.out.println("Run Designite...");

        String outputPath = this.resultsDir + System.currentTimeMillis() + "_" + folderName + "\\";

        new ProcBuilder("java")
                .withWorkingDirectory(new File(designiteDir))
                .withArg("-jar")
                .withArg("DesigniteJava.jar")
                .withArg("-i")
                .withArg(repoDir)
                .withArg("-o")
                .withArg(outputPath)
                .withNoTimeout()
                .run();
        System.out.println("Designite Done!");

        System.out.println("Read " + DESIGN_CODE_SMELLS_FILENAME);
        List<DesignSmell> designSmellList = CSVService.readCsvFile(
                outputPath + DESIGN_CODE_SMELLS_FILENAME, DesignSmell.class);
        System.out.println("Read " + IMPLEMENTATION_CODE_SMELLS_FILENAME);
        List<ImplementationSmell> implementationSmellList = CSVService.readCsvFile(
                outputPath + IMPLEMENTATION_CODE_SMELLS_FILENAME,
                ImplementationSmell.class);

        List<Smell> smellList = new ArrayList<>();
        for(DesignSmell ds : designSmellList) {
            if (Utils.allowedSmellWithRefactoringTypes.containsKey(ds.getCodeSmell())) {
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

        for(ImplementationSmell is : implementationSmellList) {
            if (Utils.allowedSmellWithRefactoringTypes.containsKey(is.getCodeSmell())) {
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
