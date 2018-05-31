package materialsdbsvc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.controllers.NoSqlController;
import co.phystech.aosorio.controllers.RequestForQuotesController;
import co.phystech.aosorio.models.ExtMaterials;
import co.phystech.aosorio.models.RequestForQuotes;

public class RFQsTest {
	
	private final static Logger slf4jLogger = LoggerFactory.getLogger(RFQsTest.class);

	private final static RequestForQuotesController ctx = new RequestForQuotesController();
	
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

		material.setItemcode("1000001");
		material.setDescription("Some tube");
		material.setType("CS");
		material.setCategory("TUBE");
		material.setDimensions("1MM X 1MM");
		material.setOrderNumber("9000");
		material.setQuantity(1.0);
		material.setUnit("EA");
		
		List<ExtMaterials> ext = new ArrayList<ExtMaterials>();
		ext.add(material);
		rfq.setMaterialList(ext);
		
		RequestForQuotesController.create(rfq);

	}
	
	@Test
	public void rfqReadTest() {

		
		Datastore datastore = NoSqlController.getInstance().getDatabase();
		
		rfqCreate();

		Query<RequestForQuotes> query = datastore.createQuery(RequestForQuotes.class);
		List<RequestForQuotes> result = query.field("internalCode").equal(1234).asList();
		
		RequestForQuotes rfq = result.iterator().next();
		slf4jLogger.info(rfq.getId().toString());
		
		assertEquals(1234, rfq.getInternalCode());
		assertEquals(1, result.size());
				
		ctx.delete(result.iterator().next());
	
	}

}
