package materialsdbsvc;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;

import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.services.CSVReader;

public class CSVReaderTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(CSVReaderTest.class);

	@Test
	public void cvsReaderTest() {
		
		CSVReader reader = new CSVReader("src/test/resources/DBMetals.csv");
		int nrows = 0;
		
		try {
			
			JsonArray metalsData = (JsonArray) reader.readFile();
			nrows = metalsData.size();
			ObjectMapper mapper = new ObjectMapper();
			
			slf4jLogger.info(metalsData.get(0).toString());
			
			Materials newFiche = mapper.readValue(metalsData.get(0).toString(), Materials.class);
		
			slf4jLogger.info(newFiche.getItemcode());
				
		} catch (FileNotFoundException e) {
			
			slf4jLogger.info("There was a problem opening the input file");
			
		} catch (JsonParseException e) {
			
			slf4jLogger.debug(e.getLocalizedMessage());
		} catch (JsonMappingException e) {
			
			slf4jLogger.debug(e.getLocalizedMessage());
		} catch (IOException e) {
			
			slf4jLogger.debug(e.getLocalizedMessage());
		}
		
		assertEquals(5,nrows);
	}

}
