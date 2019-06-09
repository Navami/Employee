package org.employee.survey.analysis;

import java.time.Period;
import java.util.Optional;

/**
 * Interface for obtaining average age from survey data at different scopes
 *
 */
public interface AgeCalculator {

    /**
     * Calculate average age of employees within a specified scope
     * @param scope enum value
     * @param id of division, team or manager, can be not present for company scope
     * @return Period of time showing the average age
     * @exception  AgeCalculatorException if id is not present for non-company scope
     */
    Period getAverageAge(Scope scope, Optional<Integer> id) throws AgeCalculatorException;

    public enum Scope {
        COMPANY,
        DIVISION,           
        TEAM,
        MANAGER
    }

    /**
     * Exception class for age calculator
     */
    class AgeCalculatorException extends Exception {
        private static final long serialVersionUID = 1L;

        AgeCalculatorException(String message) {
            super(message);
        }
        AgeCalculatorException(String message, Exception inner) {
            super(message, inner);
        }
    }   
}
