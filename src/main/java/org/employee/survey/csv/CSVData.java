package org.employee.survey.csv;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CSVData {
	 private transient final String[] fieldsInCSVHeader;

	    protected CSVData(String[] fieldsInCSVHeader) {
	        this.fieldsInCSVHeader = fieldsInCSVHeader;
	    }

	    @JsonIgnore
	    public List<String> getHeaderFields() {
	        return Arrays.asList(fieldsInCSVHeader);
	    }

	    public enum SortDirection {
	        ASCENDING,
	        DESCENDING
	    }
}
