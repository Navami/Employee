package org.employee.survey.analysis;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;

import org.employee.survey.parser.SurveyCSVParser;

/**
 * Class implementing average age calculations on survey data  
 *
 */
public final class SurveyAnalyzer implements AgeCalculator {

    private final SurveyCSVParser.SurveyCSVData surveyData;

    public SurveyAnalyzer(SurveyCSVParser.SurveyCSVData surveyData) {
        this.surveyData = surveyData;
    }

    @Override
    public Period getAverageAge(Scope scope, Optional<Integer> id) throws AgeCalculator.AgeCalculatorException {

        if (AgeCalculator.Scope.COMPANY != scope && (null == id || false == id.isPresent()))
            throw new AgeCalculator.AgeCalculatorException("For non-COMPANY scope an identifier is required");

        long totalDaysAgeOfEmployeesInScope, totalEmployeesInScope;     
        switch (scope) {

            default:    
            //case COMPANY:
                totalDaysAgeOfEmployeesInScope = surveyData.getDivisions().values().parallelStream()
                        .flatMap(d -> d.getTeams().values().parallelStream())
                        .flatMap(t -> t.getManagers().values().parallelStream())
                        .flatMap(m -> m.getEmployees().values().parallelStream())
                        .mapToLong(e -> Duration.between(e.getBirthdateDateType().atStartOfDay(), LocalDate.now().atStartOfDay()).toDays()).sum();
                totalEmployeesInScope = surveyData.getDivisions().values().parallelStream()
                        .flatMap(d -> d.getTeams().values().parallelStream())
                        .flatMap(t -> t.getManagers().values().parallelStream())
                        .flatMap(m -> m.getEmployees().values().parallelStream())
                        .count();
                break;
            case DIVISION:
                totalDaysAgeOfEmployeesInScope = surveyData.getDivisions().values().parallelStream()
                        .filter(d -> Objects.equals(d.getId(), id.get()))
                        .flatMap(d -> d.getTeams().values().parallelStream())
                        .flatMap(t -> t.getManagers().values().parallelStream())
                        .flatMap(m -> m.getEmployees().values().parallelStream())
                        .mapToLong(e -> Duration.between(e.getBirthdateDateType().atStartOfDay(), LocalDate.now().atStartOfDay()).toDays()).sum();
                totalEmployeesInScope = surveyData.getDivisions().values().parallelStream()
                        .filter(d -> Objects.equals(d.getId(), id.get()))
                        .flatMap(d -> d.getTeams().values().parallelStream())
                        .flatMap(t -> t.getManagers().values().parallelStream())
                        .flatMap(m -> m.getEmployees().values().parallelStream())
                        .count();
                break;
            case TEAM:
                totalDaysAgeOfEmployeesInScope = surveyData.getDivisions().values().parallelStream()
                        .flatMap(d -> d.getTeams().values().parallelStream())
                        .filter(t -> Objects.equals(t.getId(), id.get()))
                        .flatMap(t -> t.getManagers().values().parallelStream())
                        .flatMap(m -> m.getEmployees().values().parallelStream())
                        .mapToLong(e -> Duration.between(e.getBirthdateDateType().atStartOfDay(), LocalDate.now().atStartOfDay()).toDays()).sum();
                totalEmployeesInScope = surveyData.getDivisions().values().parallelStream()
                        .flatMap(d -> d.getTeams().values().parallelStream())
                        .filter(t -> Objects.equals(t.getId(), id.get()))
                        .flatMap(t -> t.getManagers().values().parallelStream())
                        .flatMap(m -> m.getEmployees().values().parallelStream())
                        .count();
                break;
            case MANAGER:
                totalDaysAgeOfEmployeesInScope = surveyData.getDivisions().values().parallelStream()
                        .flatMap(d -> d.getTeams().values().parallelStream())
                        .flatMap(t -> t.getManagers().values().parallelStream())
                        .filter(m -> Objects.equals(m.getId(), id.get()))
                        .flatMap(m -> m.getEmployees().values().parallelStream())
                        .mapToLong(e -> Duration.between(e.getBirthdateDateType().atStartOfDay(), LocalDate.now().atStartOfDay()).toDays()).sum();
                totalEmployeesInScope = surveyData.getDivisions().values().parallelStream()
                        .flatMap(d -> d.getTeams().values().parallelStream())
                        .flatMap(t -> t.getManagers().values().parallelStream())
                        .filter(m -> Objects.equals(m.getId(), id.get()))
                        .flatMap(m -> m.getEmployees().values().parallelStream())
                        .count();
                break;
        }

        long averageAgeDays = 0;
        if (totalEmployeesInScope > 0)
            averageAgeDays = (long)Math.floor(totalDaysAgeOfEmployeesInScope / totalEmployeesInScope);      // NOTE - Some rounding down here to nearest day over all employees in scope
        Period averageAge = Period.between(LocalDate.now(), LocalDate.now().plusDays(averageAgeDays));
        return averageAge;
    }
}
