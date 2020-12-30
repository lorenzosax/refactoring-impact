package com.group.worker;

import com.group.Utils;
import com.group.csv.DesignSmell;
import com.group.csv.ImplementationSmell;
import com.group.csv.CSVService;
import com.group.csv.Smell;
import org.apache.log4j.Logger;
import org.buildobjects.process.ProcBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DesigniteWorker {

    private static final Logger logger = Logger.getLogger(DesigniteWorker.class);

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
        this.resultsDir = Utils.preparePathOsBased(true, resultsDir, DESIGNITE_RESULTS_FOLDER);
    }

    public List<Smell> execute(String folderName) {

        logger.info("Run Designite...");

        String outputPath = Utils.preparePathOsBased(true, this.resultsDir, System.currentTimeMillis() + "_" + folderName);

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
        logger.info("Designite Done!");

        logger.info("Read " + DESIGN_CODE_SMELLS_FILENAME);
        List<DesignSmell> designSmellList = CSVService.readCsvFile(
                outputPath + DESIGN_CODE_SMELLS_FILENAME, DesignSmell.class);
        logger.info("Read " + IMPLEMENTATION_CODE_SMELLS_FILENAME);
        List<ImplementationSmell> implementationSmellList = CSVService.readCsvFile(
                outputPath + IMPLEMENTATION_CODE_SMELLS_FILENAME,
                ImplementationSmell.class);

        List<Smell> smellList = new ArrayList<>();
        for(DesignSmell ds : designSmellList) {
            if (Utils.allowedSmell.containsKey(ds.getCodeSmell())) {
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
            if (Utils.allowedSmell.containsKey(is.getCodeSmell())) {
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

        logger.info("Generating " + FILTERED_SMELLS_FILENAME);
        CSVService.writeCsvFileWithStrategy(outputPath + FILTERED_SMELLS_FILENAME, smellList, Smell.class, true, false);

        return smellList;
    }

}
