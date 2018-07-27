package materialsdbsvc;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongodb.morphia.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.controllers.CfgController;
import co.phystech.aosorio.controllers.MaterialsController;
import co.phystech.aosorio.controllers.QuotesController;
import co.phystech.aosorio.controllers.RequestForQuotesController;
import co.phystech.aosorio.exceptions.AlreadyExistsException;
import co.phystech.aosorio.models.ExtMaterials;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.models.QuotedMaterials;
import co.phystech.aosorio.models.Quotes;
import co.phystech.aosorio.models.RequestForQuotes;


public class ModelsTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ModelsTest.class);

	CfgController dbConf = new CfgController(Constants.CONFIG_FILE);

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
		
	@Test
	public void materialCreationTest() {

		String storedItem = MaterialsController.read(materialId).getItemcode();

		assertEquals(itemcode, storedItem);

		slf4jLogger.info("materialCreationTest> success");

	}

	@Test
	public void rfqCreationTest() {

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

			Key<RequestForQuotes> keys;
			keys = RequestForQuotesController.create(rfq);
			ObjectId id = (ObjectId) keys.getId();
			assertEquals(1234, RequestForQuotesController.read(id).getInternalCode());
			RequestForQuotesController.delete(rfq);
			slf4jLogger.info("rfqCreationTest> success");

		} catch (NoSuchElementException | AlreadyExistsException exception) {

			slf4jLogger.info("rfqCreationTest> Item not in DB OR RFQ already exists");
			slf4jLogger.info(exception.getLocalizedMessage());

		}

	}

	@Test
	public void quoteCreationTest() {

		Quotes quote = new Quotes();

		Date now = new Date();

		quote.setInternalCode(1234);
		quote.setExternalCode(7890);
		quote.setProcessedDate(now.toString());
		quote.setReceivedDate(now.toString());
		quote.setSentDate(now.toString());
		quote.setUser("aosorio");
		quote.setProviderCode("A012345");
		quote.setProviderName("Van Leuwen");
		quote.setContactName("Jorge Varela");

		QuotedMaterials material = new QuotedMaterials();

		material.setItemcode(itemcode);

		material.setDescription(description);
		material.setType(type);
		material.setCategory(category);
		material.setDimensions(dimensions);
		material.setOrderNumber("9000");
		material.setQuantity(1.0);
		material.setUnit("EA");
		material.setTheoreticalWeight(105.54);
		material.setGivenWeight(100.0);
		material.setUnitPrice(1.0);
		material.setTotalPrice(100.0);

		List<QuotedMaterials> ext = new ArrayList<QuotedMaterials>();
		ext.add(material);
		quote.setMaterialList(ext);

		Key<Quotes> keys;

		try {
			keys = QuotesController.create(quote);
			ObjectId id = (ObjectId) keys.getId();
			assertEquals(1234, QuotesController.read(id).getInternalCode());
			QuotesController.delete(quote);
			slf4jLogger.info("quoteCreationTest> success");

		} catch (NoSuchElementException | AlreadyExistsException exception) {

			slf4jLogger.info("quoteCreationTest> Item not in DB OR QUOTE already exists");
			slf4jLogger.info(exception.getLocalizedMessage());

		}

		slf4jLogger.info("quoteCreationTest> DONE");

	}

}
