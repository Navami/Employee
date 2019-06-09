package org.employee.survey.csv;

import java.util.Optional;

import org.employee.survey.exception.MalformedCSVException;

/*
 * Interface defining CSV parser functions
 * 
 */
public interface Parser {
    /**
     * Parse CSV file into an object structure
     * @return CSV data object
     * @throws MalformedCSVException 
     */
    Optional<CSVData> parse() throws MalformedCSVException ;
}
