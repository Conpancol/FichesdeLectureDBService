package materialsdbsvc;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.morphia.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.config.Constants;
import co.phystech.aosorio.controllers.CfgController;
import co.phystech.aosorio.controllers.MaterialsController;
import co.phystech.aosorio.controllers.QuotesController;
import co.phystech.aosorio.controllers.RequestForQuotesController;
import co.phystech.aosorio.models.ExtMaterials;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.models.QuotedMaterials;
import co.phystech.aosorio.models.Quotes;
import co.phystech.aosorio.models.RequestForQuotes;

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

		Materials material = new Materials();

		material.setItemcode(itemcode);
		material.setDescription(description);
		material.setType(type);
		material.setCategory(category);
		material.setDimensions(dimensions);

		MaterialsController ctx = new MaterialsController();
		Key<Materials> keys = ctx.create(material);
		ObjectId id = (ObjectId) keys.getId();

		assertEquals(itemcode, ctx.read(id).getItemcode());

		ctx.delete(material);

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
		
		RequestForQuotesController ctx = new RequestForQuotesController();
		Key<RequestForQuotes> keys = RequestForQuotesController.create(rfq);
		ObjectId id = (ObjectId) keys.getId();
		
		assertEquals(1234, ctx.read(id).getInternalCode());
		
		ctx.delete(rfq);

		slf4jLogger.info("rfqCreationTest> success");
		
	}
	
	@Test
	public void quoteCreationTest() {

		Quotes quote = new Quotes();

		Date now = new Date();

		quote.setInternalCode(1234);
		quote.setExternalCode(7890);
		quote.setProcessedDate(now);
		quote.setReceivedDate(now);
		quote.setSentDate(now);
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
		
		QuotesController ctx = new QuotesController();
		Key<Quotes> keys = ctx.create(quote);
		ObjectId id = (ObjectId) keys.getId();
		
		assertEquals(1234, ctx.read(id).getInternalCode());
		
		ctx.delete(quote);

		slf4jLogger.info("quoteCreationTest> success");
		
	}
	

}
