package com.group.csv;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.group.csv.strategy.HeaderColumnNameAndOrderMappingStrategy;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CSVService {

    public static <T> List readCsvFile(String filename, Class<T> clazz) {
        List<T> csvSmellList = null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filename));
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withSkipLines(1)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withType(clazz)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            csvSmellList = csvToBean.parse();
            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }
        return csvSmellList;
    }

    public static <T> boolean writeCsvFile(String filename, List<T> items, Class<T> clazz) {

        try {
            Writer writer = Files.newBufferedWriter(Paths.get(filename));
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            beanToCsv.write(items);
            writer.close();
            return true;
        } catch (Exception e) {
            System.err.println(e);
        }
        return false;
    }

    public static <T> boolean writeCsvFileWithStrategy(String filename, List<T> items, Class<T> clazz) {

        try {
            Writer writer = Files.newBufferedWriter(Paths.get(filename));
            HeaderColumnNameAndOrderMappingStrategy<T> strategy = new HeaderColumnNameAndOrderMappingStrategy<>(clazz);
            StatefulBeanToCsvBuilder<T> builder = new StatefulBeanToCsvBuilder(writer);
            StatefulBeanToCsv beanWriter = builder
                    .withMappingStrategy(strategy)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withEscapechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            beanWriter.write(items);
            writer.close();
            return true;
        } catch (Exception e) {
            System.err.println(e);
        }
        return false;
    }
}
