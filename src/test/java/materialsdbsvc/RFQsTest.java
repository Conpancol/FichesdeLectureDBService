package materialsdbsvc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.controllers.MaterialsController;
import co.phystech.aosorio.controllers.NoSqlController;
import co.phystech.aosorio.controllers.RequestForQuotesController;
import co.phystech.aosorio.exceptions.AlreadyExistsException;
import co.phystech.aosorio.models.ExtMaterials;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.models.RequestForQuotes;

public class RFQsTest {
	
	private final static Logger slf4jLogger = LoggerFactory.getLogger(RFQsTest.class);

	public static final String itemcode = "TEST0001";
	public static final String description = "TUBE, CS, 1\"";
	public static final String type = "CS";
	public static final String category = "TUBE";
	public static final String dimensions = "1\"";
	
	private static ObjectId materialId;
	
	@BeforeClass
	public static void beforeClass() { 
		
		Materials material = new Materials();

		material.setItemcode(itemcode);
		material.setDescription(description);
		material.setType(type);
		material.setCategory(category);
		material.setDimensions(dimensions);

		Key<Materials> keys = MaterialsController.create(material);
		
		materialId = (ObjectId) keys.getId();
		
	}
	
	@AfterClass
	public static void afterClass() {
		
		Materials material = MaterialsController.read(materialId);
		
		MaterialsController.delete(material);

	}
	
	private void rfqCreate() {

		RequestForQuotes rfq = new RequestForQuotes();

		Date now = new Date();

		rfq.setInternalCode(1234);
		rfq.setExternalCode(7890);
		rfq.setProcessedDate(now.toString());
		rfq.setReceivedDate(now.toString());
		rfq.setUser("aosorio");
		rfq.setNote("Material para proyecto Serpentines");

		ExtMaterials material = new ExtMaterials();

		material.setItemcode(itemcode);
		material.setDescription(description);
		material.setType(type);
		material.setCategory(category);
		material.setDimensions(dimensions);
		
		material.setOrderNumber("9000");
		material.setQuantity(1.0);
		material.setUnit("EA");
		
		List<ExtMaterials> ext = new ArrayList<ExtMaterials>();
		ext.add(material);
		rfq.setMaterialList(ext);
		
		try {
			
			RequestForQuotesController.create(rfq);
			
		} catch (NoSuchElementException | AlreadyExistsException exception) {

			slf4jLogger.info("rfqCreate> Item not in DB OR RFQ already exists");
			slf4jLogger.info(exception.getLocalizedMessage());

		}

	}
	
	@Test
	public void rfqReadTest() {

		
		Datastore datastore = NoSqlController.getInstance().getDatabase();
		
		rfqCreate();

		Query<RequestForQuotes> query = datastore.createQuery(RequestForQuotes.class);
		List<RequestForQuotes> result = query.field("internalCode").equal(1234).asList();
		
		if ( ! result.isEmpty() ) {
			RequestForQuotes rfq = result.iterator().next();
		
			slf4jLogger.info(rfq.getId().toString());
		
			assertEquals(1234, rfq.getInternalCode());
			assertEquals(1, result.size());
				
			RequestForQuotesController.delete(result.iterator().next());
			
		}
	
	}

}
