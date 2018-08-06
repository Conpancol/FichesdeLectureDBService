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

import org.apache.commons.math3.exception.MathParseException;
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
import co.phystech.aosorio.controllers.FittingsController;
import co.phystech.aosorio.controllers.NoSqlController;
import co.phystech.aosorio.models.ExtQuotedMaterials;
import co.phystech.aosorio.models.Fittings;
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
	public void materialsCounterTest() {

		// Test the book counter service
		Request pRequest = null;
		Response pResponse = null;

		JsonObject json = (JsonObject) StatisticsSvc.getBasicStats(pRequest, pResponse);

		slf4jLogger.info("Number of materials: " + json.get("materials"));

		assertTrue(json.has("materials"));

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

		List<ExtQuotedMaterials> pipes = ExtQuotedMaterialsController.readBy("category", "PIPE");
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

			JsonReader jsonReader = new JsonReader(
					new FileReader(ClassLoader.getSystemResource(Constants.CONFIG_DENSITIES_FILE).getPath()));
			jsonReader.beginArray();
			Gson gson = new Gson();

			int idx = 0;
			while (jsonReader.hasNext()) {
				Densities item = gson.fromJson(jsonReader, Densities.class);
				assertEquals(types.get(idx), item.type);
				idx += 1;
				if (idx == types.size())
					break;
			}

			jsonReader.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	@Test
	public void getDensityTest() {

		slf4jLogger.info(String.valueOf(Utilities.getDensity("SS")));
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

			try {

				ArrayList<Double> dims = Utilities.getHollowBarDimsMM(material);

				slf4jLogger.debug("*X= " + quoted.getType() + "\t" + String.valueOf(dims.get(0)) + "\t"
						+ String.valueOf(dims.get(1)) + "\t"
						+ String.valueOf(GeneralSvc.calculateMaterialWeight(quoted)));

			} catch ( MathParseException ex) {

				slf4jLogger.info(" PARSE ERROR AT HOLLOW*: " + material.getItemcode());

			}

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
			quoted.setUnit("M2");

			try {
				
				if( !material.getDimensions().contains("MM"))
					continue;
				
				ArrayList<Double> dims = Utilities.getPlateDimsMM(material);

				double weight = GeneralSvc.calculateMaterialWeight(quoted);

				slf4jLogger.debug("*PLATE*: " + material.getDimensions());

				slf4jLogger.debug("*PLATE*: " + quoted.getType() + "\t" + String.valueOf(dims.get(0)) + "\t"
						+ String.valueOf(dims.get(1)) + "\t" + String.valueOf(dims.get(2)) + "\t"
						+ String.valueOf(weight));

			} catch (MathParseException ex) {

				slf4jLogger.info(" PARSE ERROR at PLATE: " + material.getItemcode());
				
			} catch ( Exception ex) {
				slf4jLogger.info(ex.getMessage());
				slf4jLogger.info(" ERROR at: " + material.getItemcode());
			}

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
			} catch (NullPointerException ex) {
				dims = Utilities.getBeamDimsMM(material);
			}

			double weight = GeneralSvc.calculateMaterialWeight(quoted);

			slf4jLogger.debug("*CHANNEL*: " + material.getDimensions());

			slf4jLogger.debug("*CHANNEL*: " + quoted.getType() + "\t" + String.format("%.2f", dims.get(0)) + "\t"
					+ String.format("%.2f", dims.get(1)) + "\t" + String.format("%.2f", dims.get(2)) + "\t"
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
			} catch (NullPointerException ex) {
				dims = Utilities.getBeamDimsMM(material);
			}

			double weight = GeneralSvc.calculateMaterialWeight(quoted);

			slf4jLogger.debug("*ANGLE*: " + material.getDimensions());

			slf4jLogger.debug("*ANGLE*: " + quoted.getType() + "\t" + String.format("%.2f", dims.get(0)) + "\t"
					+ String.format("%.2f", dims.get(1)) + "\t" + String.format("%.2f", dims.get(2)) + "\t"
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

		slf4jLogger.info("*PLATE*: " + quoted.getType() + "\t" + String.valueOf(dims.get(0)) + "\t"
				+ String.valueOf(dims.get(1)) + "\t" + String.valueOf(dims.get(2)) + "\t" + String.valueOf(weight));

	}

	@Test
	public void elbowParsingTest() {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);
		List<Materials> result = query.field("category").equal("ELBOW").asList();

		Iterator<Materials> itr = result.iterator();

		while (itr.hasNext()) {

			Materials material = itr.next();

			for (String elbow : Constants.FITTING_ELBOWS) {

				boolean xcheck = Utilities.checkFittingCategory(elbow, material.getDescription(), 3);
				if (xcheck) {
					slf4jLogger.debug("*ELBOW*: " + material.getDescription());
					slf4jLogger.debug("*ELBOW*: " + elbow);
				}

			}

		}

	}

	@Test
	public void elbowWeightTest() {

		QuotedMaterials quoted = new QuotedMaterials();
		quoted.setDescription("ELBOW, 90º, CS,6\", SCH40,BW,LR,SMLS, ASTM A234 GR WPB");
		quoted.setDimensions("6\", SCH40");

		String clean_dsc = quoted.getDescription().replaceAll("[^a-zA-Z0-9\",]", "");

		slf4jLogger.info("*ELBOW*: " + clean_dsc);

		for (String category : Constants.FITTING_ELBOWS) {

			boolean xcheck = Utilities.checkFittingCategory(category, quoted.getDescription(), 3);

			if (xcheck) {

				String schedule = Utilities.getPipeSchedule(quoted);
				String pipeSize = Utilities.getDimensionINCH(quoted).get(0);

				Fittings fitting = FittingsController.read(category, schedule, pipeSize);

				double weight = fitting.getWeight();

				slf4jLogger.debug("*ELBOW*: " + quoted.getDescription());
				slf4jLogger.debug("*ELBOW*: " + schedule);
				slf4jLogger.debug("*ELBOW*: " + Utilities.getDimensionINCH(quoted));
				slf4jLogger.debug("*ELBOW*: " + String.valueOf(weight));

			}
		}
	}

	@Test
	public void allElbowsWeightTest() {

		datastore = NoSqlController.getInstance().getDatabase();

		Query<Materials> query = datastore.createQuery(Materials.class);
		List<Materials> result = query.field("category").equal("ELBOW").asList();

		Iterator<Materials> itr = result.iterator();

		while (itr.hasNext()) {

			Materials material = itr.next();
			QuotedMaterials quoted = new QuotedMaterials();

			quoted.setDescription(material.getDescription());
			quoted.setDimensions(material.getDimensions());
			quoted.setItemcode(material.getItemcode());
			quoted.setCategory(material.getCategory());
			quoted.setQuantity(10.0);

			double weight = Utilities.getFittingWeight(quoted, Constants.FITTING_ELBOWS, 3);
			double weight_total = GeneralSvc.calculateMaterialWeight(quoted);

			if (weight > 0.0) {

				slf4jLogger.debug("*ELBOW*: " + quoted.getDescription());
				slf4jLogger.debug("*ELBOW*: " + quoted.getItemcode());
				slf4jLogger.debug("*ELBOW*: " + Utilities.getPipeSchedule(quoted));
				slf4jLogger.debug("*ELBOW*: " + Utilities.getDimensionINCH(quoted));
				slf4jLogger.debug("*ELBOW*: " + String.valueOf(weight));
				slf4jLogger.debug("*ELBOW*: " + String.valueOf(weight_total));
			}

		}

	}

	@Test
	public void hastelloyTubeTest() {

		Supplier<FormulaFactory> formulaFactory = FormulaFactory::new;

		Formula formula = formulaFactory.get().getFormula("CYLINDERVOL");

		double wt = 0.035;
		double od = 0.5;
		double id = 0.5 - (2.0 * wt);

		formula.addVariable("OD", od * Constants.UNIT_INCH_to_MM * Constants.UNIT_MM_to_M);
		formula.addVariable("ID", id * Constants.UNIT_INCH_to_MM * Constants.UNIT_MM_to_M);
		formula.addVariable("H", 16.459);

		double volume = formula.eval();
		double density = Utilities.getDensity("HASTELLOY") * Constants.UNIT_KG_o_M3;
		double weight = volume * density;

		slf4jLogger.info("Tubing Hastelloy volume: " + String.valueOf(volume));
		slf4jLogger.info("Tubing Hastelloy weight: " + String.valueOf(weight));
		assertEquals(4.85377, weight, 0.001);

		Materials material = new Materials();
		material.setDimensions("16\",SCH40");

		formula.addVariable("OD", Utilities.getODMM(material) * Constants.UNIT_MM_to_M);
		formula.addVariable("ID", Utilities.getIDMM(material) * Constants.UNIT_MM_to_M);
		formula.addVariable("H", 24.00);

		volume = formula.eval();
		density = Utilities.getDensity("TITANIUM") * Constants.UNIT_KG_o_M3;
		weight = volume * density;

		slf4jLogger.info("Tubing Titanium volume: " + String.valueOf(volume));
		slf4jLogger.info("Tubing Titanium weight: " + String.valueOf(weight));
		assertEquals(1696.45, weight, 0.01);

		formula.addVariable("OD", 80.0 * Constants.UNIT_MM_to_M);
		formula.addVariable("H", 2.00);

		volume = formula.eval();
		density = Utilities.getDensity("TITANIUM") * Constants.UNIT_KG_o_M3;
		weight = volume * density;

		slf4jLogger.info("BAR Titanium volume: " + String.valueOf(volume));
		slf4jLogger.info("BAR Titanium weight: " + String.valueOf(weight));
		assertEquals(45.30, weight, 0.1);

	}

}
