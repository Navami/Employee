package org.employee.survey.analysis;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.employee.survey.csv.CSVData;
import org.employee.survey.parser.SurveyCSVParser;

import org.junit.Test;

public class SurveyAnalyzerTests {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");

    @Test
    public void test_CompanyScope_success() throws AgeCalculator.AgeCalculatorException {

        SurveyCSVParser.SurveyCSVData surveyData = buildSampleData();

        SurveyAnalyzer analysis = new SurveyAnalyzer(surveyData);
        Period period = analysis.getAverageAge(AgeCalculator.Scope.COMPANY, Optional.empty());
        assertNotNull(period);
        assertEquals(1, period.getDays());      
    }

    @Test
    public void test_NoEmployees_success() throws AgeCalculator.AgeCalculatorException {

        SurveyCSVParser.SurveyCSVData surveyData = buildEmptySampleData();

        SurveyAnalyzer analysis = new SurveyAnalyzer(surveyData);
        Period period = analysis.getAverageAge(AgeCalculator.Scope.COMPANY, Optional.empty());
        assertNotNull(period);
        assertTrue(period.equals(Period.ZERO));     
    }

    @Test
    public void test_DivisionScope_success() throws AgeCalculator.AgeCalculatorException {

        SurveyCSVParser.SurveyCSVData surveyData = buildSampleData();

        SurveyAnalyzer analysis = new SurveyAnalyzer(surveyData);
        Period period = analysis.getAverageAge(AgeCalculator.Scope.DIVISION, Optional.of(1));
        assertNotNull(period);
        assertEquals(1, period.getDays());      
    }

    @Test(expected=AgeCalculator.AgeCalculatorException.class)
    public void test_DivisionScope_failure() throws AgeCalculator.AgeCalculatorException {

        SurveyCSVParser.SurveyCSVData surveyData = buildEmptySampleData();

        SurveyAnalyzer analysis = new SurveyAnalyzer(surveyData);
        analysis.getAverageAge(AgeCalculator.Scope.DIVISION, null);     
    }

    private SurveyCSVParser.SurveyCSVData buildSampleData() {

        SurveyCSVParser.SurveyCSVData surveyData = new SurveyCSVParser.SurveyCSVData(SurveyCSVParser.SurveyCSVData.Employee.SortOrder.ORIGINAL);

        SurveyCSVParser.SurveyCSVData.Division division = new SurveyCSVParser.SurveyCSVData.Division(1, SurveyCSVParser.SurveyCSVData.Employee.SortOrder.ORIGINAL);
        SurveyCSVParser.SurveyCSVData.Team team = division.createTeam(1);
        SurveyCSVParser.SurveyCSVData.Manager manager = team.createManager(1, CSVData.SortDirection.ASCENDING);
        SurveyCSVParser.SurveyCSVData.Employee employee1 = manager.createEmployee(1, "Stuart", "Mackintosh", LocalDate.now().minusDays(1).format(FORMATTER));
        SurveyCSVParser.SurveyCSVData.Employee employee2 = manager.createEmployee(2, "Stuart L", "Mackintosh", LocalDate.now().minusDays(2).format(FORMATTER));

        manager.addEmployee(1, employee1);
        manager.addEmployee(2, employee2);
        team.addManager(1, manager);
        division.addTeam(1, team);
        surveyData.addDivision(1, division);

        return surveyData;
    }

    private SurveyCSVParser.SurveyCSVData buildEmptySampleData() {

        SurveyCSVParser.SurveyCSVData surveyData = new SurveyCSVParser.SurveyCSVData(SurveyCSVParser.SurveyCSVData.Employee.SortOrder.ORIGINAL);

        SurveyCSVParser.SurveyCSVData.Division division = new SurveyCSVParser.SurveyCSVData.Division(1, SurveyCSVParser.SurveyCSVData.Employee.SortOrder.ORIGINAL);
        SurveyCSVParser.SurveyCSVData.Team team = division.createTeam(1);
        SurveyCSVParser.SurveyCSVData.Manager manager = team.createManager(1, CSVData.SortDirection.ASCENDING);

        team.addManager(1, manager);
        division.addTeam(1, team);
        surveyData.addDivision(1, division);

        return surveyData;      
    }
}
