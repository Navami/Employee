package org.employee.application;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;

import org.employee.survey.analysis.AgeCalculator;
import org.employee.survey.analysis.AgeCalculator.AgeCalculatorException;
import org.employee.survey.analysis.SurveyAnalyzer;
import org.employee.survey.csv.CSVData;
import org.employee.survey.csv.CSVParser;
import org.employee.survey.csv.Parser;
import org.employee.survey.parser.SurveyCSVParser;
import org.employee.survey.json.JSONWriter;


/**
 * Main class with entry point
 *
 */
class Application {

    /**
     * Main entry point
     * @param args
     */
    public static void main(String[] args) {

        try {
            // Path to supplied CSV data file
            Path csvFilePath = Paths.get("data", "data.csv");
          
            // Process command-line arguments for sort order and direction
            org.employee.survey.parser.SurveyCSVParser.SurveyCSVData.Employee.SortOrder employeeSortOrder = processSortOrder(args);
            org.employee.survey.csv.CSVData.SortDirection employeeSortDirection = processSortAscendingDescending(args);

            // Create the parser
            Parser csvParser = CSVParser.createSurveyParser(csvFilePath.toFile(), employeeSortOrder, employeeSortDirection);

            long timeBeforeWorkMs = System.nanoTime();

            // Parse into object structure
            Optional<CSVData> csvDataObjectsOrNull = csvParser.parse();     
            if (true == csvDataObjectsOrNull.isPresent())
            {
                CSVData csvDataObjects = csvDataObjectsOrNull.get();
                Objects.requireNonNull(csvDataObjects, "FAILED to parse CSV");

                // Create the writer
                JSONWriter writerofJson = new JSONWriter(csvDataObjects);

                // Write out objects as JSON
                String jsonStringRepresentation = writerofJson.write();
                Objects.requireNonNull(jsonStringRepresentation, "FAILED to output JSON");

                System.out.println("Processed in " + (System.nanoTime() - timeBeforeWorkMs) + "ms");

                // Dump JSON to console
                System.out.println("JSON formatted survey data");
                System.out.println(jsonStringRepresentation);                           // NOTE - Verify and pretty print JSON output at https://jsonlint.com/

                // Check we have survey data
                if (true == csvDataObjects instanceof SurveyCSVParser.SurveyCSVData) {

                    SurveyCSVParser.SurveyCSVData surveyData = (SurveyCSVParser.SurveyCSVData)csvDataObjects;

                    // Dump some sample object data to console
                    SurveyCSVParser.SurveyCSVData.Manager sampleManager = surveyData.getDivisions().get(1).getTeams().get(5).getManagers().get(1);
                    System.out.println("Division 1, Team 5, Manager 1 has employees: " + sampleManager);

                    try {
                        // Create survey data analyzer
                        AgeCalculator averageAgeCalculator = new SurveyAnalyzer(surveyData);
                        Period averageAge;

                        // Calculate some sample average ages and dump to console
                        averageAge = averageAgeCalculator.getAverageAge(AgeCalculator.Scope.COMPANY, Optional.empty());
                        System.out.println("Average age of employees in company: " + formatPeriod(averageAge));

                        averageAge = averageAgeCalculator.getAverageAge(AgeCalculator.Scope.DIVISION, Optional.of(1));      // NOTE - Samples only, not added to command line arguments
                        System.out.println("Average age of employees in division 1: " + formatPeriod(averageAge));

                        averageAge = averageAgeCalculator.getAverageAge(AgeCalculator.Scope.TEAM, Optional.of(12));
                        System.out.println("Average age of employees in team 12: " + formatPeriod(averageAge));

                        averageAge = averageAgeCalculator.getAverageAge(AgeCalculator.Scope.MANAGER, Optional.of(2));
                        System.out.println("Average age of employees under manager 2: " + formatPeriod(averageAge));
                    }
                    catch (AgeCalculatorException e) {
                        System.out.println("AGE EXCEPTION: " + e.toString());
                    }
                }
                else {
                    System.out.println("UNEXPECTED CSV data type");
                }
            }
            else {
                System.out.println("FAILED to parse CSV data");
            }

            System.out.flush();
        }
        catch (Exception e) {
            System.out.println("EXCEPTION: " + e.toString());
        }
    }

    private static org.employee.survey.parser.SurveyCSVParser.SurveyCSVData.Employee.SortOrder processSortOrder(String[] args) {

    	org.employee.survey.parser.SurveyCSVParser.SurveyCSVData.Employee.SortOrder sortOrder = org.employee.survey.parser.SurveyCSVParser.SurveyCSVData.Employee.SortOrder.ORIGINAL;

        if (args.length > 0) {
            try {
                sortOrder = Enum.valueOf(org.employee.survey.parser.SurveyCSVParser.SurveyCSVData.Employee.SortOrder.class, args[0]);             
            }
            catch (IllegalArgumentException e) {
                System.out.println("FAILED to process sort order, defaulting to ORIGINAL");
            }
        }
        System.out.println("Sort order is " + sortOrder.name());

        return sortOrder;
    }

    private static org.employee.survey.csv.CSVData.SortDirection processSortAscendingDescending(String[] args) {

    	org.employee.survey.csv.CSVData.SortDirection sortDirection = org.employee.survey.csv.CSVData.SortDirection.ASCENDING;

        if (args.length > 1) {
            if (true == "DESC".equalsIgnoreCase(args[1]))
                sortDirection = org.employee.survey.csv.CSVData.SortDirection.DESCENDING;         
        }
        System.out.println("Sort direction is " + sortDirection.name());

        return sortDirection;       
    }

    private static String formatPeriod(Period period) {
        String formattedPeriod = String.format("%d years, %d months, %d days", period.getYears(), period.getMonths(), period.getDays());
        return formattedPeriod;
    }
}
