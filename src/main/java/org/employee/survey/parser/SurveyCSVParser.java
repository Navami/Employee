package org.employee.survey.parser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.employee.survey.csv.CSVData;
import org.employee.survey.csv.CSVParser;
import org.employee.survey.csv.Parser;
import org.employee.survey.exception.MalformedCSVException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class for parsing CSV data related to employee survey
 *
 */
public final class SurveyCSVParser extends CSVParser implements Parser {

    private static final short MIN_TOKENS_PER_LINE = 7;
    private static final String SEPERATOR_OF_TOKENS = ",";

    private final SurveyCSVData.Employee.SortOrder sortOrderOfDataOrEmployees;
    private final CSVData.SortDirection sortDirectionOfEmployees;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");

    public SurveyCSVParser(File csvFile, SurveyCSVData.Employee.SortOrder sortOrderOfDataOrEmployees, CSVData.SortDirection sortDirectionOfEmployees) {
        super(csvFile, MIN_TOKENS_PER_LINE, SEPERATOR_OF_TOKENS);
        this.sortOrderOfDataOrEmployees = sortOrderOfDataOrEmployees;
        this.sortDirectionOfEmployees = sortDirectionOfEmployees;
    }

    @Override
    public Optional<CSVData> parse() throws MalformedCSVException{
        SurveyCSVData csvDataParsed = null;

        if (fileExists() && fileIsCorrectlyFormatted()) {
            List<String> linesOfCSV = fileLines();
            SurveyCSVData csvData = new SurveyCSVData(sortOrderOfDataOrEmployees);
            try {
                if (SurveyCSVData.Employee.SortOrder.ORIGINAL != sortOrderOfDataOrEmployees)
                    linesOfCSV.parallelStream().forEach(l -> processLineOfCSV(l, csvData, sortOrderOfDataOrEmployees, sortDirectionOfEmployees));       
                else
                    linesOfCSV.stream().forEach(l -> processLineOfCSV(l, csvData, sortOrderOfDataOrEmployees, sortDirectionOfEmployees));   
                csvDataParsed = csvData;
            }
            catch (Exception e) {                       
                throw new MalformedCSVException("FAILED to parse CSV file");     // NOTE - Should a "bad" line prevent the remainder of the parse?
            }
        }

        return Optional.ofNullable(csvDataParsed);
    }

    private static void processLineOfCSV(String line, SurveyCSVData data, SurveyCSVData.Employee.SortOrder sortOrderOfDataOrEmployees, CSVData.SortDirection sortDirectionOfEmployees)
    {
        StringTokenizer tokenizer = new StringTokenizer(line, SEPERATOR_OF_TOKENS);
        short indexOfTokenFound = 0;
        String divisionId = null, teamId = null, managerId = null, employeeId = null, lastName = null, firstName = null, birthdate = null;      
        while (tokenizer.hasMoreTokens() && indexOfTokenFound < MIN_TOKENS_PER_LINE) {

            String token = tokenizer.nextToken();
            switch (indexOfTokenFound) {
                case 0:
                    divisionId = token;
                    break;
                case 1:
                    teamId = token;
                    break;
                case 2:
                    managerId = token;
                    break;
                case 3:
                    employeeId = token;
                    break;
                case 4:
                    firstName = token;
                    break;
                case 5:
                    lastName = token;
                    break;
                case MIN_TOKENS_PER_LINE-1:
                    birthdate = token;
                    break;
                default:
                    assert false;
            }
            indexOfTokenFound++;
        }

        if (indexOfTokenFound >= MIN_TOKENS_PER_LINE)
            buildSurveyData(divisionId, teamId, managerId, employeeId, firstName, lastName, birthdate, data, sortOrderOfDataOrEmployees, sortDirectionOfEmployees);
    }

    private static synchronized void buildSurveyData(String divisionId, String teamId, String managerId, String employeeId, String firstName, String lastName, String birthdate, SurveyCSVData data, SurveyCSVData.Employee.SortOrder sortOrderOfDataOrEmployees, CSVData.SortDirection direction) 
    {
        Objects.requireNonNull(divisionId);
        Objects.requireNonNull(teamId);
        Objects.requireNonNull(managerId);
        Objects.requireNonNull(employeeId);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(birthdate);

        Integer divisionIdBox = Integer.parseInt(divisionId);
        Integer teamIdBox = Integer.parseInt(teamId);
        Integer managerIdBox = Integer.parseInt(managerId);
        Integer employeeIdBox = Integer.parseInt(employeeId);

        if (false == data.divisions.containsKey(divisionIdBox))
             data.divisions.put(divisionIdBox, new SurveyCSVData.Division(divisionIdBox, sortOrderOfDataOrEmployees));
        SurveyCSVData.Division division = data.divisions.get(divisionIdBox);

        if (false == division.teams.containsKey(teamIdBox))
            division.teams.put(teamIdBox, division.createTeam(teamIdBox));
        SurveyCSVData.Team team = division.teams.get(teamIdBox);

        if (false == team.managers.containsKey(managerIdBox))
            team.managers.put(managerIdBox, team.createManager(managerIdBox, direction));
        SurveyCSVData.Manager manager = team.managers.get(managerIdBox);

        if (false == manager.employees.containsKey(employeeIdBox)) 
            manager.employees.put(employeeIdBox, manager.createEmployee(employeeIdBox, firstName, lastName, birthdate));        // NOTE - Duplicates will not be added more than once
    }

    /**
     * 
     * Class representing survey data
     *
     */
    public final static class SurveyCSVData extends CSVData {

        private static final short VERSION = 1;                 // NOTE - Good idea to apply version to data structures

        private Map<Integer, Division> divisions;

        public SurveyCSVData(Employee.SortOrder sortOrderOfDataOrEmployees) {
            super(new String[] {"divisionId", "teamId", "managerId", "employeeId", "lastName", "firstName", "birthdate"});
            if (Employee.SortOrder.ORIGINAL == sortOrderOfDataOrEmployees)
                divisions = new LinkedHashMap <>();
            else
                divisions = new TreeMap<>();
        }

        public void addDivision(Integer id, Division division) {
            Objects.requireNonNull(id); Objects.requireNonNull(division);
            divisions.put(id, division);
        }

        public Map<Integer, Division>  getDivisions() {
            return Collections.unmodifiableMap(divisions);  
        }

        /**
         * Class representing division in survey data
         */
        public final static class Division {

            private Map<Integer, Team> teams;

            private transient final Integer id;
            private final Employee.SortOrder sortOrderOfDataOrEmployees;

            public Division(Integer id, Employee.SortOrder sortOrderOfDataOrEmployees) {
                this.id = id;
                this.sortOrderOfDataOrEmployees = sortOrderOfDataOrEmployees;
                if (Employee.SortOrder.ORIGINAL == sortOrderOfDataOrEmployees)
                    teams = new LinkedHashMap <>();
                else
                    teams = new TreeMap<>();
            }

            @JsonIgnore
            public Integer getId() {
                return id; 
            }   

            public void addTeam(Integer id, Team team) {
                Objects.requireNonNull(id); Objects.requireNonNull(team);
                teams.put(id, team);
            }
            public Team createTeam(Integer id) {
                return new Team(id, sortOrderOfDataOrEmployees);
            }

            public Map<Integer, Team> getTeams() {
                return Collections.unmodifiableMap(teams);  
            }       
        }

        /**
         * Class representing team in survey data
         */
        public final static class Team {

            private Map<Integer, Manager> managers;

            private transient final Integer id;
            private final Employee.SortOrder sortOrderOfDataOrEmployees;

            public Team(Integer id, Employee.SortOrder sortOrderOfDataOrEmployees) {
                this.id = id;
                this.sortOrderOfDataOrEmployees = sortOrderOfDataOrEmployees;
                if (Employee.SortOrder.ORIGINAL == sortOrderOfDataOrEmployees)
                    managers = new LinkedHashMap <>();
                else
                    managers = new TreeMap<>();
            }

            @JsonIgnore
            public Integer getId() {
                return id; 
            }

            public void addManager(Integer id, Manager manager) {
                Objects.requireNonNull(id); Objects.requireNonNull(manager);
                managers.put(id, manager);
            }           
            public Manager createManager(Integer id, CSVData.SortDirection sortDirectionOfEmployees) {
                return new Manager(id, sortOrderOfDataOrEmployees, sortDirectionOfEmployees);
            }

            public Map<Integer, Manager> getManagers() {
                return Collections.unmodifiableMap(managers);   
            }           
        }

        /**
         * Class representing manager in survey data
         */
        public final static class Manager {

            private final Employee.SortOrder sortOrderOfDataOrEmployees;
            private final CSVData.SortDirection sortDirectionOfEmployees;

            private transient Map<Integer, Employee> employees;

            private transient final Integer id;

            public Manager(Integer id, Employee.SortOrder sortOrderOfDataOrEmployees, CSVData.SortDirection sortDirectionOfEmployees) {
                this.id = id;
                this.sortOrderOfDataOrEmployees = sortOrderOfDataOrEmployees;
                this.sortDirectionOfEmployees = sortDirectionOfEmployees;
                if (Employee.SortOrder.ORIGINAL == sortOrderOfDataOrEmployees)
                    employees = new LinkedHashMap <>();
                else
                    employees = new TreeMap<>();
            }

            @JsonIgnore
            public Integer getId() {
                return id; 
            }   

            public void addEmployee(Integer id, Employee employee) {
                Objects.requireNonNull(id); Objects.requireNonNull(employee);
                employees.put(id, employee);
            }
            public Employee createEmployee(Integer id, String firstName, String lastName, String birthdate) {
                return new Employee(id, firstName, lastName, birthdate);
            }

            public Map<Integer, Employee> getEmployees() {
                return Collections.unmodifiableMap(employees);  
            }

            @JsonProperty("employees")
            public Map<Integer, Employee> getOrderedEmployees() {

                Map<Integer, Employee> orderedMapOfEmployees;
                if ((Employee.SortOrder.ID == sortOrderOfDataOrEmployees && CSVData.SortDirection.ASCENDING == sortDirectionOfEmployees) || Employee.SortOrder.ORIGINAL == sortOrderOfDataOrEmployees)
                    orderedMapOfEmployees = employees;
                else {
                    Comparator<Integer> valueComparator = (k1, k2) -> {
                          Employee e1 = employees.get(k1);
                          Employee e2 = employees.get(k2);
                          int compare = 0;
                          if(null != e1 && null != e2) {
                              switch (sortOrderOfDataOrEmployees) {
                                case ID:
                                    compare = Integer.valueOf(e1.id).compareTo(Integer.valueOf(e2.id)); 
                                    break;      
                                case LASTNAME:
                                    compare = e1.lastName.compareTo(e2.lastName);       
                                    break;                          
                                case FIRSTNAME:
                                    compare = e1.firstName.compareTo(e2.firstName);     
                                    break;  
                                case BIRTHDATE:
                                    compare = e1.birthdate.compareTo(e2.birthdate);     
                                    break;
                                default:
                                    assert false;
                                    break;
                              }
                              if (CSVData.SortDirection.DESCENDING == sortDirectionOfEmployees)
                                  compare = -compare;
                          }
                          else
                              throw new NullPointerException("Comparator does not support null values");
                          return compare;
                    };

                    Map<Integer, Employee> sortedMapOfEmployees = new TreeMap<>(valueComparator);
                    sortedMapOfEmployees.putAll(employees);
                    orderedMapOfEmployees = sortedMapOfEmployees;
                }
                return orderedMapOfEmployees;
            }

            @Override                   
            public String toString() {
                return Objects.toString(employees); 
            }
        }

        /**
         * Class representing employee in survey data
         */
        public final static class Employee {

            private final int id;
            private final String firstName;
            private final String lastName;          
            private final String birthdate;

            private transient final LocalDate birthdateDateType;

            public Employee(int id, String firstName, String lastName, String birthdate) {
                this.id = id;
                this.firstName = firstName;
                this.lastName = lastName;
                this.birthdate = birthdate;
                this.birthdateDateType = LocalDate.parse(birthdate, FORMATTER);         // NOTE - Formatter is not thread safe
            }

            public int getId() {
                return id;
            }
            public String getFirstName() {
                return firstName;
            }
            public String getLastName() {
                return lastName;
            }
            public String getBirthdate() {
                return birthdate;
            }
            @JsonIgnore
            public LocalDate getBirthdateDateType() {
                return birthdateDateType;
            }

            @Override                   
            public String toString() {
                return "(id='" + id + "', firstName='" + firstName + "', lastName='" + lastName + "', birthdate='" + birthdate + "')";
            }

            public enum SortOrder {
                ORIGINAL,
                ID,
                LASTNAME,
                FIRSTNAME,
                BIRTHDATE
            }
        }       
    }
}