import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import csv.CSVDesignSmell;
import csv.CSVImplementationSmell;
import org.buildobjects.process.ProcBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesigniteWorker {

    private static final String DESIGNITE_RESULTS_FOLDER = "designiteResults";
    private static final String DESIGN_CODE_SMELLS_FILENAME = "designCodeSmells.csv";
    private static final String IMPLEMENTATION_CODE_SMELLS_FILENAME = "implementationCodeSmells.csv";
    private static final String FILTEREDSMELLS_FILENAME = "filteredSmells.csv";

    static final Map<String, Boolean> ALLOWED_SMELL = new HashMap<>() {{
       put("Long Method", true);
       put("Long Parameter List", true);
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
        return DESIGNITE_RESULTS_FOLDER + "\\";
    }

    public List<Smell> execute(String folderName) {

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

        List<CSVDesignSmell> designSmellList = readCsvFile(outputPath + DESIGN_CODE_SMELLS_FILENAME, CSVDesignSmell.class);
        List<CSVImplementationSmell> implementationSmellList =
                readCsvFile(outputPath + IMPLEMENTATION_CODE_SMELLS_FILENAME, CSVImplementationSmell.class);

        List<Smell> smellList = new ArrayList<>();
        for(CSVDesignSmell ds : designSmellList) {
            if (ALLOWED_SMELL.containsKey(ds.getCodeSmell())) {
                smellList.add(new Smell(ds.getProjectName(), ds.getPackageName(), ds.getClassName(), null, ds.getCodeSmell()));
            }
        }

        for(CSVImplementationSmell is : implementationSmellList) {
            if (ALLOWED_SMELL.containsKey(is.getCodeSmell())) {
                smellList.add(new Smell(is.getProjectName(), is.getPackageName(), is.getClassName(), is.getMethodName(), is.getCodeSmell()));
            }
        }

        try {
            // TODO not working: no data write on file (but file is generated)
            /*
            Writer writer = Files.newBufferedWriter(Paths.get(designitePath + "\\" + outputPath + FILTEREDSMELLS_FILENAME));
            ColumnPositionMappingStrategy mappingStrategy= new ColumnPositionMappingStrategy();
            mappingStrategy.setType(Smell.class);
            String[] columns = new String[]{ "Project", "Package", "Class", "Method name", "Code Smell" };
            mappingStrategy.setColumnMapping(columns);

            StatefulBeanToCsvBuilder<Smell> builder = new StatefulBeanToCsvBuilder(writer);
            StatefulBeanToCsv beanWriter = builder.withMappingStrategy(mappingStrategy).withSeparator(',').build();
            beanWriter.write(smellList);
            */

            Writer writer = Files.newBufferedWriter(Paths.get(designitePath + "\\" + outputPath + FILTEREDSMELLS_FILENAME));
            StatefulBeanToCsv<Smell> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            beanToCsv.write(smellList);
        } catch (Exception e) {
            System.err.println(e);
        }
        return smellList;
    }

    public <T> List readCsvFile(String filename, Class<T> clazz) {
        List<T> csvSmellList = null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(designitePath + "\\" + filename));
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withSkipLines(1)
                    .withSeparator(',')
                    .withType(clazz)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            csvSmellList = csvToBean.parse();
        } catch (Exception e) {
            System.err.println(e);
        }
        return csvSmellList;
    }

}
