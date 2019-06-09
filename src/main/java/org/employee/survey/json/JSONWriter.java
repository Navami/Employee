package org.employee.survey.json;

import java.io.IOException;

import org.employee.survey.csv.CSVData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Class for writing JSON data from object graph via Jackson libraries
 *
 */
public final class JSONWriter {

    private final CSVData csvData;

    public JSONWriter(CSVData csvData) {
        this.csvData = csvData;
    }

    public String write() throws JsonProcessingException, IOException {
    	String jsonStringRepresentation = null;
        ObjectMapper objectToJsonMapper = new ObjectMapper();
       
         jsonStringRepresentation = objectToJsonMapper.writeValueAsString(csvData);   
       
        return jsonStringRepresentation;
    }
}
