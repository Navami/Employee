package org.employee.survey.json;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.employee.survey.csv.CSVData;
import org.employee.survey.parser.SurveyCSVParser;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JSONWriterTests {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");

    @Test
    public void testWrite_success() {

        SurveyCSVParser.SurveyCSVData surveyData = buildSampleData();
        JSONWriter writerOfJson = null;
       
        try {
        	 writerOfJson = new JSONWriter(surveyData);
            String jsonString = writerOfJson.write();
            assertNotNull(jsonString);
            assertTrue(jsonString.length() > 0);
        }
        catch (JsonProcessingException e) {
            fail("JSON processing failed");
        }
        catch(Exception e)  
        {  
         System.out.println("Parent Exception occurs");  
        } 
    }

    private SurveyCSVParser.SurveyCSVData buildSampleData() {

        SurveyCSVParser.SurveyCSVData surveyData = new SurveyCSVParser.SurveyCSVData(SurveyCSVParser.SurveyCSVData.Employee.SortOrder.ORIGINAL);

        SurveyCSVParser.SurveyCSVData.Division division = new SurveyCSVParser.SurveyCSVData.Division(1, SurveyCSVParser.SurveyCSVData.Employee.SortOrder.ORIGINAL);
        SurveyCSVParser.SurveyCSVData.Team team = division.createTeam(1);
        SurveyCSVParser.SurveyCSVData.Manager manager = team.createManager(1, CSVData.SortDirection.ASCENDING);
        SurveyCSVParser.SurveyCSVData.Employee employee = manager.createEmployee(1, "Stuart", "Mackintosh", LocalDate.now().minusDays(1).format(FORMATTER));

        manager.addEmployee(1, employee);
        team.addManager(1, manager);
        division.addTeam(1, team);
        surveyData.addDivision(1, division);

        return surveyData;
    }
}
