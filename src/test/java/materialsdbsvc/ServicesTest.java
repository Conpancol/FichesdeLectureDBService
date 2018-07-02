/**
 * 
 */
package materialsdbsvc;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import co.phystech.aosorio.controllers.ExtQuotedMaterialsController;
import co.phystech.aosorio.controllers.NoSqlController;
import co.phystech.aosorio.models.ExtQuotedMaterials;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.models.QuotedMaterials;
import co.phystech.aosorio.services.Formula;
import co.phystech.aosorio.services.FormulaFactory;
import co.phystech.aosorio.services.GeneralSvc;
import co.phystech.aosorio.services.OpenExchangeSvc;
import co.phystech.aosorio.services.StatisticsSvc;
import co.phystech.aosorio.services.Utilities;
import spark.Request;
import spark.Response;

/**
 * @author AOSORIO
 *
 */
public class ServicesTest {

	public class Densities {

		String type;
		double density;

	}

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ServicesTest.class);

	private static Datastore datastore;

	@Test
	public void bookCounterTest() {

		// Test the book counter service
		Request pRequest = null;
		Response pResponse = null;

		JsonObject json = (JsonObject) StatisticsSvc.getBasicStats(pRequest, pResponse);

		slf4jLogger.info("Number of books: " + json.get("books"));
		slf4jLogger.info("Number of comments: " + json.get("comments"));

		assertTrue(json.has("books"));

	}

	@Test
	public void countGroupTest() {

		// Test the book counter service
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
			if (json.isJsonObject()) {
				slf4jLogger.info(json.toString());
				if (json.has("origin"))
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

		Supplier<FormulaFactory> formulaFactory = FormulaFactory::new;

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
		material.setQuantity(108);

		Supplier<FormulaFactory> formulaFactory = FormulaFactory::new;

		Formula formula = formulaFactory.get().getFormula("CYLINDERVOL");

		slf4jLogger.info(formula.getName());

		double outerDiam = Utilities.getODMM(material) / 1000.0;
		double innerDiam = Utilities.getIDMM(material) / 1000.0;

		String info = material.getDimensions() + "\t" + String.valueOf(outerDiam) + "\t" + String.valueOf(innerDiam);

		slf4jLogger.info(info);

		formula.addVariable("OD", outerDiam);
		formula.addVariable("ID", innerDiam);
		formula.addVariable("H", material.getQuantity());

		double density = 8.0 * 1000.0; // kg/m3

		double volume = formula.eval();

		double weight = volume * density;

		slf4jLogger.info(String.valueOf(weight));

		assertEquals(275.417, weight, 0.001);

	}

	@Test
	public void theoreticalWeightsTest() {
		
		List<ExtQuotedMaterials> pipes = ExtQuotedMaterialsController.read("PIPE");
		Iterator<ExtQuotedMaterials> itrPipes = pipes.iterator();
		
		while(itrPipes.hasNext()) {
			ExtQuotedMaterials material = itrPipes.next();
			double weight = GeneralSvc.calculateMaterialWeight(material);
			String info = "* " + material.getItemcode() +
					"\t" + material.getDimensions() +
					"\t" + material.getType() +
					"\t" + String.format("%.2f", weight) +
					"\t" + String.valueOf(material.getGivenWeight());
					
			slf4jLogger.info(info);
		}
	}
	
	@Test
	public void scheduleFinderTest() {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);
		List<Materials> result = query.field("category").equal("PIPE").asList();

		Iterator<Materials> itr = result.iterator();

		while (itr.hasNext()) {

			Materials material = itr.next();

			double outerDiam = Utilities.getODMM(material);
			double innerDiam = Utilities.getIDMM(material);
			String info = material.getDimensions() 
					+ "\t" + String.format("%.2f",outerDiam) 
					+ "\t" + String.format("%.2f", innerDiam);
			slf4jLogger.info(info);

		}

		assertTrue(true);

	}

	@Test
	public void getDensitiesTest() {

		ArrayList<String> types = new ArrayList<String>();
		types.add("SS");
		types.add("HASTELLOY");
		types.add("CS");
		
		try {

			JsonReader jsonReader = new JsonReader(new FileReader("src/test/resources/materials.json"));
			jsonReader.beginArray();
			Gson gson = new Gson();
			
			int idx = 0;
			while (jsonReader.hasNext()) {
				Densities item = gson.fromJson(jsonReader, Densities.class);
				assertEquals(types.get(idx),item.type);
				idx += 1;
			}

			jsonReader.endArray();
			jsonReader.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	@Test
	public void getDensityTest() {
		
		assertEquals( 8.00, Utilities.getDensity("SS"), 0.001);
		assertEquals( 8.94, Utilities.getDensity("HASTELLOY"), 0.001);
		assertEquals( 7.85, Utilities.getDensity("CS"), 0.001);
		
	}

}
