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

import co.phystech.aosorio.config.Constants;
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

		slf4jLogger.debug("Number of books: " + json.get("books"));
		slf4jLogger.debug("Number of comments: " + json.get("comments"));

		assertTrue(json.has("books"));

	}

	@Test
	public void countGroupTest() {

		// Test the book counter service
		Request pRequest = null;
		Response pResponse = null;

		JsonObject json = (JsonObject) StatisticsSvc.getAdvancedStats(pRequest, pResponse);

		slf4jLogger.debug("Groups " + json.get("groups"));

		assertTrue(true);

	}

	@Test
	public void readJsonFromUrlTest() {

		try {

			JsonObject json = GeneralSvc.readJsonFromUrl("https://httpbin.org/get");
			if (json.isJsonObject()) {
				slf4jLogger.debug(json.toString());
				if (json.has("origin"))
					slf4jLogger.debug(json.get("origin").toString());

			} else {
				slf4jLogger.debug("null object");

			}

		} catch (JsonParseException | IOException e) {
			slf4jLogger.debug("IOException");

		}

		assertTrue(true);

	}

	@Test
	public void openExchangeTest() {

		double usdTRM = OpenExchangeSvc.getUSDTRM();
		slf4jLogger.debug(String.valueOf(usdTRM));
		assertTrue(true);

	}

	@Test
	public void formulasTest() {

		Supplier<FormulaFactory> formulaFactory = FormulaFactory::new;

		Formula formula = formulaFactory.get().getFormula("CYLINDERVOL");

		slf4jLogger.debug(formula.getName());

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

		slf4jLogger.debug(formula.getName());

		double outerDiam = Utilities.getODMM(material) / 1000.0;
		double innerDiam = Utilities.getIDMM(material) / 1000.0;

		String info = material.getDimensions() + "\t" + String.valueOf(outerDiam) + "\t" + String.valueOf(innerDiam);

		slf4jLogger.debug(info);

		formula.addVariable("OD", outerDiam);
		formula.addVariable("ID", innerDiam);
		formula.addVariable("H", material.getQuantity());

		double density = 8.0 * 1000.0; // kg/m3

		double volume = formula.eval();

		double weight = volume * density;

		slf4jLogger.debug(String.valueOf(weight));

		assertEquals(275.417, weight, 0.001);

	}

	@Test
	public void theoreticalWeightsTest() {

		List<ExtQuotedMaterials> pipes = ExtQuotedMaterialsController.read("PIPE");
		Iterator<ExtQuotedMaterials> itrPipes = pipes.iterator();

		while (itrPipes.hasNext()) {
			ExtQuotedMaterials material = itrPipes.next();
			double weight = GeneralSvc.calculateMaterialWeight(material);
			String info = "* " + material.getItemcode() + "\t" + material.getDimensions() + "\t" + material.getType()
					+ "\t" + String.format("%.2f", weight) + "\t" + String.valueOf(material.getGivenWeight());

			slf4jLogger.debug(info);
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
			String info = material.getDimensions() + "\t" + String.format("%.2f", outerDiam) + "\t"
					+ String.format("%.2f", innerDiam);
			slf4jLogger.debug(info);

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
				assertEquals(types.get(idx), item.type);
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

		assertEquals(8.00, Utilities.getDensity("SS"), 0.001);
		assertEquals(8.94, Utilities.getDensity("HASTELLOY"), 0.001);
		assertEquals(7.85, Utilities.getDensity("CS"), 0.001);

	}

	@Test
	public void parsingDimensionTest() {

		double x1 = Utilities.parseDimension("1/2\"");
		assertEquals(0.5 * Constants.UNIT_INCH_to_MM, x1, 0.1);

		double x2 = Utilities.parseDimension("12\"");
		assertEquals(12.0 * Constants.UNIT_INCH_to_MM, x2, 0.1);

		double x3 = Utilities.parseDimension("7/8\"");
		assertEquals(0.875 * Constants.UNIT_INCH_to_MM, x3, 0.1);

	}

	@Test
	public void barVolumeTest() {

		QuotedMaterials material = new QuotedMaterials();
		material.setDescription("BAR,ROUND,MEDIUM CS 10MM,1/2\",AISI C1045");
		material.setDimensions("MEDIUM CS 10MM,1/2\"");
		material.setCategory("BAR");
		material.setType("SS");
		material.setQuantity(100);

		double diameter = Utilities.getBarODMM(material);

		assertEquals(0.5 * Constants.UNIT_INCH_to_MM, diameter, 0.01);

		double volume = GeneralSvc.calculateMaterialWeight(material);

		assertEquals(101.341, volume, 0.001);

	}

	@Test
	public void hollowBarVolumeTest() {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);
		List<Materials> result = query.field("description").contains("HOLLOW").asList();

		Iterator<Materials> itr = result.iterator();

		while (itr.hasNext()) {

			Materials material = itr.next();

			QuotedMaterials quoted = new QuotedMaterials();
			quoted.setDescription(material.getDescription());
			quoted.setDimensions(material.getDimensions());
			quoted.setCategory(material.getCategory());
			quoted.setType(material.getType());
			quoted.setQuantity(100.0);

			slf4jLogger.debug("*HOLLOW*: " + material.getDimensions());
			ArrayList<Double> dims = Utilities.getHollowBarDimsMM(material);

			slf4jLogger.debug("*X= " + quoted.getType() + "\t" + String.valueOf(dims.get(0)) + "\t"
					+ String.valueOf(dims.get(1)) + "\t" + String.valueOf(GeneralSvc.calculateMaterialWeight(quoted)));

		}

	}

	@Test
	public void platesVolumeTest() {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);
		List<Materials> result = query.field("category").contains("PLATE").asList();

		Iterator<Materials> itr = result.iterator();

		while (itr.hasNext()) {

			Materials material = itr.next();

			QuotedMaterials quoted = new QuotedMaterials();
			quoted.setDescription(material.getDescription());
			quoted.setDimensions(material.getDimensions());
			quoted.setCategory(material.getCategory());
			quoted.setType(material.getType());
			quoted.setQuantity(100.0);
			
			ArrayList<Double> dims = Utilities.getPlateDimsMM(material);

			double weight = GeneralSvc.calculateMaterialWeight(quoted);
			
			slf4jLogger.debug("*PLATE*: " + material.getDimensions());
			
			slf4jLogger.debug("*PLATE*: " + quoted.getType() + "\t" 
					+ String.valueOf(dims.get(0)) + "\t"
					+ String.valueOf(dims.get(1)) + "\t" 
					+ String.valueOf(dims.get(2)) + "\t" 
					+ String.valueOf(weight));

		}

	}
	
	@Test
	public void channelVolumeTest() {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);
		List<Materials> result = query.field("category").equal("BEAM").asList();

		Iterator<Materials> itr = result.iterator();

		while (itr.hasNext()) {

			Materials material = itr.next();

			QuotedMaterials quoted = new QuotedMaterials();
			quoted.setDescription(material.getDescription());
			quoted.setDimensions(material.getDimensions());
			quoted.setCategory(material.getCategory());
			quoted.setType(material.getType());
			quoted.setQuantity(100.0);
			
			ArrayList<Double> dims = null;
			
			try {
				dims = Utilities.getBeamDimsINCH(material);
			} catch ( NullPointerException ex) {
				dims = Utilities.getBeamDimsMM(material);
			}
			
			double weight = GeneralSvc.calculateMaterialWeight(quoted);
			
			slf4jLogger.debug("*CHANNEL*: " + material.getDimensions());
			
			slf4jLogger.debug("*CHANNEL*: " + quoted.getType() + "\t" 
					+ String.format("%.2f", dims.get(0)) + "\t"
					+ String.format("%.2f", dims.get(1)) + "\t" 
					+ String.format("%.2f", dims.get(2)) + "\t" 
					+ String.format("%.2f", weight));

		}

	}
	
	@Test
	public void angleVolumeTest() {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);
		List<Materials> result = query.field("category").equal("ANGLE").asList();

		Iterator<Materials> itr = result.iterator();

		while (itr.hasNext()) {

			Materials material = itr.next();

			QuotedMaterials quoted = new QuotedMaterials();
			quoted.setDescription(material.getDescription());
			quoted.setDimensions(material.getDimensions());
			quoted.setCategory(material.getCategory());
			quoted.setType(material.getType());
			quoted.setQuantity(100.0);
			
			ArrayList<Double> dims = null;
			
			try {
				dims = Utilities.getBeamDimsINCH(material);
			} catch ( NullPointerException ex) {
				dims = Utilities.getBeamDimsMM(material);
			}
			
			double weight = GeneralSvc.calculateMaterialWeight(quoted);
			
			slf4jLogger.debug("*ANGLE*: " + material.getDimensions());
			
			slf4jLogger.debug("*ANGLE*: " + quoted.getType() + "\t" 
					+ String.format("%.2f", dims.get(0)) + "\t"
					+ String.format("%.2f", dims.get(1)) + "\t" 
					+ String.format("%.2f", dims.get(2)) + "\t" 
					+ String.format("%.2f", weight));

		}

	}
	
	@Test
	public void platesVolumeCalculationTest() {

		QuotedMaterials quoted = new QuotedMaterials();
		quoted.setDescription("PLATE, SS316L, 4' X 8' X 1/32\", 1219.2MM X 2438.4MM X 0.79MM");
		quoted.setDimensions("4' X 8' X 1/32\", 1219.2MM X 2438.4MM X 0.79MM");
		quoted.setCategory("PLATE");
		quoted.setType("SS");
		quoted.setQuantity(11.88);
		quoted.setUnit("M2");
		
		ArrayList<Double> dims = Utilities.getPlateDimsMM(quoted);

		double weight = GeneralSvc.calculateMaterialWeight(quoted);
			
		slf4jLogger.info("*PLATE*: " + quoted.getDimensions());
		
		slf4jLogger.info("*PLATE*: " + quoted.getType() + "\t" 
				+ String.valueOf(dims.get(0)) + "\t"
				+ String.valueOf(dims.get(1)) + "\t" 
				+ String.valueOf(dims.get(2)) + "\t" 
				+ String.valueOf(weight));
			
	}
	
	
}
