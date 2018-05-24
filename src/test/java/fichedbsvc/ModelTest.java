package fichedbsvc;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;
import org.sql2o.converters.UUIDConverter;
import org.sql2o.quirks.PostgresQuirks;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.controllers.CfgController;
import co.phystech.aosorio.controllers.NoSqlController;
import co.phystech.aosorio.models.Materials;

public class ModelTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ModelTest.class);

	CfgController dbConf = new CfgController(Constants.CONFIG_FILE);

	public static final String itemcode = "1234567890";
	public static final String description = "TUBE, CS, 1\"";
	public static final String type = "CS";
	public static final String category = "TUBE";
	public static final String dimensions = "1\"";
	
	@Test
	public void materialCreationTest() {
		
		NoSqlController dbcontroller = NoSqlController.getInstance();
		
		final Datastore datastore = dbcontroller.getDatabase();
	
		Materials material = new Materials();

		material.setItemcode(itemcode);;
		material.setDescription(description);
		material.setType(type);
		material.setCategory(category);
		material.setDimensions(dimensions);

		//UUID id = 		
		//assertTrue(test);
		//model.deleteBook(id);
		
		slf4jLogger.info("materialCreationTest> success");
		
	}
	
}
