/**
 * 
 */
package materialsdbsvc;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.controllers.ExtQuotedMaterialsController;
import co.phystech.aosorio.models.ExtQuotedMaterials;
import co.phystech.aosorio.models.QuotedMaterials;

/**
 * @author AOSORIO
 *
 */
public class ExQuotedMaterialsTest {
	
	private final static Logger slf4jLogger = LoggerFactory.getLogger(ExQuotedMaterialsTest.class);

	@Test
	public void materialCreationTest() {

		QuotedMaterials quoted = new QuotedMaterials();
		
		quoted.setItemcode("10001");
		quoted.setDescription("Full DESCRIPTION");
		quoted.setDimensions("1MM X 1MM");
		quoted.setType("SX");
		quoted.setCategory("TUBE");
		quoted.setOrderNumber("101");
		quoted.setUnit("M");
		quoted.setQuantity(1.0);
		quoted.setTheoreticalWeight(1.0);
		quoted.setGivenWeight(2.0);
		quoted.setUnitPrice(3.0);
		quoted.setTotalPrice(3.0);
		quoted.setNote("Testing material");
		
		ExtQuotedMaterials material = new ExtQuotedMaterials(quoted);
		
		material.setProviderId("CN101");
		material.setProviderName("Chinese provider name");
		material.setProjectId(101);
		material.setRevision(0);
		
		Date now = new Date();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String updateDate = formatter.format(now);
		slf4jLogger.info("Today is: " + updateDate);
		material.setUpdateDate(updateDate);
		
		boolean result = ExtQuotedMaterialsController.create(material);
		
		assertTrue(result);
		
		int delResult = ExtQuotedMaterialsController.delete(material).getN();
		slf4jLogger.info(String.valueOf(delResult));
		assertEquals(1, delResult);

	}

}
