package org.employee.survey.csv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.*;

import org.employee.survey.parser.SurveyCSVParser;
import org.employee.survey.parser.SurveyCSVParser.SurveyCSVData;

/**
 * Base class for CSV parsers
 *
 */
public class CSVParser {

    private final File csvFile;
    private final short minimumFieldsPerLine;
    private final String seperatorOfFields;

    private List<String> linesOfCSVFile;

    protected CSVParser(File csvFile, short minimumFieldsPerLine, String seperatorOfFields) {
        this.csvFile = csvFile;
        this.minimumFieldsPerLine = minimumFieldsPerLine;
        this.seperatorOfFields = seperatorOfFields;
    }

    public static Parser createSurveyParser(File csvFile, SurveyCSVData.Employee.SortOrder order, CSVData.SortDirection direction) {
        Objects.requireNonNull(csvFile);
        return new SurveyCSVParser(csvFile, order, direction);
    }
    public static Parser createSurveyParser(File csvFile) {
        return new SurveyCSVParser(csvFile, SurveyCSVData.Employee.SortOrder.ORIGINAL, CSVData.SortDirection.ASCENDING);
    }

    protected boolean fileExists() {
        return csvFile.exists() && csvFile.canRead();
    }
    protected boolean fileIsCorrectlyFormatted() {
        readFile();     
        return linesOfCSVFile.size() > 0 && linesOfCSVFile.get(0).split(seperatorOfFields).length >= minimumFieldsPerLine;
    }
    protected List<String> fileLines() {
        readFile();
        return linesOfCSVFile.stream().skip(1).collect(toList());
    }

    private synchronized void readFile()
    {
        try {
            if (null == linesOfCSVFile) {
                if (true == fileExists())
                    linesOfCSVFile = Files.readAllLines(csvFile.toPath());      // NOTE - BufferedReader may be preferred for very large files, can then process line by line or in chunks...
            }
        }
        catch (IOException e) {
            // NOTE - Retry in a limited loop, ...
            throw new RuntimeException("FAILED to read file content");
        }
    }
}
