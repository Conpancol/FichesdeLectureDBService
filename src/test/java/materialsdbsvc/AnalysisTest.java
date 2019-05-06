package materialsdbsvc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongodb.morphia.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.controllers.MaterialsController;
import co.phystech.aosorio.controllers.RequestForQuotesController;
import co.phystech.aosorio.exceptions.AlreadyExistsException;
import co.phystech.aosorio.models.ExtMaterials;
import co.phystech.aosorio.models.Materials;
import co.phystech.aosorio.models.RequestForQuotes;

public class AnalysisTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(AnalysisTest.class);

	private static final String itemcode_01 = "TEST0001";
	private static final String description_01 = "TUBE 1 , CS, 1\"";
	private static final String type_01 = "CS";
	private static final String category_01 = "TUBE";
	private static final String dimensions_01 = "1\"";
	private static ObjectId materialId_01;
	private static Materials material_01;

	private static final String itemcode_02 = "TEST0002";
	private static final String description_02 = "TUBE 2, SS, 1\"";
	private static final String type_02 = "SS";
	private static final String category_02 = "TUBE";
	private static final String dimensions_02 = "1\"";
	private static ObjectId materialId_02;
	private static Materials material_02;

	private static RequestForQuotes rfq;

	@BeforeClass
	public static void beforeClass() {

		material_01 = new Materials();
		material_01.setItemcode(itemcode_01);
		material_01.setDescription(description_01);
		material_01.setType(type_01);
		material_01.setCategory(category_01);
		material_01.setDimensions(dimensions_01);

		Key<Materials> keys = MaterialsController.create(material_01);
		materialId_01 = (ObjectId) keys.getId();

		material_02 = new Materials();
		material_02.setItemcode(itemcode_02);
		material_02.setDescription(description_02);
		material_02.setType(type_02);
		material_02.setCategory(category_02);
		material_02.setDimensions(dimensions_02);

		keys = MaterialsController.create(material_02);
		materialId_02 = (ObjectId) keys.getId();

		rfqCreate();

	}

	@AfterClass
	public static void afterClass() {

		Materials material = MaterialsController.read(materialId_01);
		MaterialsController.delete(material);

		material = MaterialsController.read(materialId_02);
		MaterialsController.delete(material);

		RequestForQuotesController.delete(rfq);

	}

	private static void rfqCreate() {

		rfq = new RequestForQuotes();

		Date now = new Date();

		rfq.setInternalCode(1234);
		rfq.setExternalCode(7890);
		rfq.setProcessedDate(now.toString());
		rfq.setReceivedDate(now.toString());
		rfq.setUser("aosorio");
		rfq.setNote("Material para proyecto Serpentines");

		List<ExtMaterials> ext = new ArrayList<ExtMaterials>();

		ExtMaterials extMaterial_01 = new ExtMaterials(material_01);
		extMaterial_01.setOrderNumber("9000");
		extMaterial_01.setQuantity(1.0);
		extMaterial_01.setUnit("EA");
		slf4jLogger.info("rfqCreate> " + extMaterial_01.getDescription());
		ext.add(extMaterial_01);

		ExtMaterials extMaterial_02 = new ExtMaterials(material_02);
		extMaterial_02.setOrderNumber("9001");
		extMaterial_02.setQuantity(1.0);
		extMaterial_02.setUnit("EA");
		slf4jLogger.info("rfqCreate> " + extMaterial_02.getDescription());
		ext.add(extMaterial_02);

		rfq.setMaterialList(ext);

		try {

			RequestForQuotesController.create(rfq);

		} catch (NoSuchElementException | AlreadyExistsException exception) {

			slf4jLogger.info("rfqCreate> Item not in DB OR RFQ already exists");
			slf4jLogger.info(exception.getLocalizedMessage());
		}

	}

	@Test
	public void basicAnalysis() {

		RequestForQuotes rfqSorted = RequestForQuotesController.readSortByType(1234);

		try {
			rfqSorted.setInternalCode(12345);
			RequestForQuotesController.create(rfqSorted);

		} catch (NoSuchElementException | AlreadyExistsException exception) {

			slf4jLogger.info("rfqCreate> Item not in DB OR RFQ already exists");
			slf4jLogger.info(exception.getLocalizedMessage());
		}

		List<ExtMaterials> materialList = rfqSorted.getMaterialList();

		assertEquals(materialList.size(), 2);
		assertEquals(12345, rfqSorted.getInternalCode());
		assertEquals(7890, rfqSorted.getExternalCode());
		assertEquals("aosorio", rfqSorted.getUser());
		assertEquals("Material para proyecto Serpentines", rfqSorted.getNote());

		Iterator<ExtMaterials> itrMaterial = materialList.iterator();

		while (itrMaterial.hasNext()) {

			ExtMaterials material = itrMaterial.next();
			slf4jLogger.info(material.getType() + " " + material.getDescription());

		}

	}

}
