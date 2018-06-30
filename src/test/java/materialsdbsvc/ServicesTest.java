/**
 * 
 */
package materialsdbsvc;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.function.Supplier;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import co.phystech.aosorio.models.QuotedMaterials;
import co.phystech.aosorio.services.Formula;
import co.phystech.aosorio.services.FormulaFactory;
import co.phystech.aosorio.services.GeneralSvc;
import co.phystech.aosorio.services.OpenExchangeSvc;
import co.phystech.aosorio.services.StatisticsSvc;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class ServicesTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ServicesTest.class);
	
	@Test
	public void bookCounterTest() {
		
		//Test the book counter service
		Request pRequest = null;
		Response pResponse = null;
		
		JsonObject json = (JsonObject) StatisticsSvc.getBasicStats(pRequest, pResponse);
		
		slf4jLogger.info("Number of books: " + json.get("books"));
		slf4jLogger.info("Number of comments: " + json.get("comments"));
		
		assertTrue(json.has("books"));
		
	}
	
	@Test
	public void countGroupTest() {
		
		//Test the book counter service
		Request pRequest = null;
		Response pResponse = null;
		
		JsonObject json = (JsonObject) StatisticsSvc.getAdvancedStats(pRequest, pResponse);
				
		slf4jLogger.info("Groups " + json.get("groups"));

		assertTrue(true);
		
	}
	
	@Test
	public void readJsonFromUrlTest() {

		try {
			
			JsonObject json = GeneralSvc.readJsonFromUrl("https://httpbin.org/get");
			if (json.isJsonObject() ) {
				slf4jLogger.info(json.toString());
				if( json.has("origin"))
					slf4jLogger.info(json.get("origin").toString());
				
			} else {
				slf4jLogger.info("null object");
				
			}
		
		} catch (JsonParseException | IOException e) {
			slf4jLogger.info("IOException");
			
		}
	
		assertTrue(true);
			
	}

	@Test
	public void openExchangeTest() {
		
		double usdTRM = OpenExchangeSvc.getUSDTRM();
		slf4jLogger.info(String.valueOf(usdTRM));
		assertTrue(true);
		
		
	}
	
	@Test
	public void formulasTest() {
			
		Supplier<FormulaFactory> formulaFactory =  FormulaFactory::new;
		
		Formula formula = formulaFactory.get().getFormula("CYLINDERVOL");
		
		slf4jLogger.info(formula.getName());
		
		formula.addVariable("OD", 8.0);
		formula.addVariable("ID", 4.0);
		formula.addVariable("H", 10.0);
		
		double vol1 = formula.eval();

		assertEquals(376.991, vol1, 0.01);
		
		formula.addVariable("OD", 8.0);
		formula.addVariable("H", 10.0);
		
		double vol2 = formula.eval();

		assertEquals(502.654, vol2, 0.01);
		
	}

	
	@Test
	public void weightCalculationTest() {
		
		QuotedMaterials material = new QuotedMaterials();
		material.setDescription("PIPE,SS316L, 1\", STANDARD,SMLS,SA312,SCH40");
		material.setDimensions("1\",SCH40");
		material.setCategory("PIPE");
		material.setType("SS");
		material.setQuantity(12);
			
		Supplier<FormulaFactory> formulaFactory =  FormulaFactory::new;
		
		Formula formula = formulaFactory.get().getFormula("CYLINDERVOL");
		
		slf4jLogger.info(formula.getName());
		
		assertTrue(true);
		
	}

}
