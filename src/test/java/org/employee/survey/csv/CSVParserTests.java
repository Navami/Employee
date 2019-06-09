package org.employee.survey.csv;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.employee.survey.exception.MalformedCSVException;
import org.employee.survey.parser.SurveyCSVParser.SurveyCSVData;
import org.junit.Test;

public class CSVParserTests {

    @Test
    public void testParse_givenCSV_success() {

        Path csvFilePath = Paths.get("data", "data.csv");

        Parser csvParser = CSVParser.createSurveyParser(csvFilePath.toFile());
        assertNotNull(csvParser);

        Optional<CSVData> csvData = null;
		try {
			csvData = csvParser.parse();
		} catch (MalformedCSVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        assertNotNull(csvData.orElse(null));
        assertTrue(csvData.get() instanceof SurveyCSVData);
        SurveyCSVData surveyData = (SurveyCSVData)csvData.get();
        assertNotNull(surveyData.getDivisions());
        assertNotNull(surveyData.getDivisions().get(1));
    }

  
    @Test
    public void testParse_extraCSV_success() {

        Path csvFilePath = Paths.get("data", "data.csv");

        Parser csvParser = CSVParser.createSurveyParser(csvFilePath.toFile());
        assertNotNull(csvParser);

        Optional<CSVData> csvData = null;
		try {
			csvData = csvParser.parse();
		} catch (MalformedCSVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        assertNotNull(csvData.orElse(null));
        assertTrue(csvData.get() instanceof SurveyCSVData);
        SurveyCSVData surveyData = (SurveyCSVData)csvData.get();
        assertNotNull(surveyData.getDivisions());
        assertNotNull(surveyData.getDivisions().get(1));
    }

    @Test
    public void testParse_badCSV_failure() {

        Path csvFilePath = Paths.get("data", "badformat.csv");

        Parser csvParser = CSVParser.createSurveyParser(csvFilePath.toFile());
        assertNotNull(csvParser);

        Optional<CSVData> csvData = null;
		try {
			csvData = csvParser.parse();
		} catch (MalformedCSVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        assertNull(csvData.orElse(null));
    }

    @Test
    public void testParse_nonExistantCSV_failure() {

        Path csvFilePath = Paths.get("data", "missing.csv");

        Parser csvParser = CSVParser.createSurveyParser(csvFilePath.toFile());
        assertNotNull(csvParser);

        Optional<CSVData> csvData = null;
		try {
			csvData = csvParser.parse();
		} catch (MalformedCSVException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        assertNull(csvData.orElse(null));
    }
}