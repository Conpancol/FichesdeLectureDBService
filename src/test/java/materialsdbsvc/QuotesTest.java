package materialsdbsvc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import co.phystech.aosorio.controllers.ExtQuotedMaterialsController;
import co.phystech.aosorio.models.ExtQuotedMaterials;
import co.phystech.aosorio.models.QuotedMaterials;
import co.phystech.aosorio.services.QuotesAnalysisSvc;
import co.phystech.aosorio.services.Utilities;

public class QuotesTest {

	private static List<ExtQuotedMaterials> todb;
	private static QuotedMaterials material;
	
	@BeforeClass
	public static void beforeClass() { 
		
		material = new QuotedMaterials();

		material.setItemcode("TEST001");
		material.setDescription("SOME PIPE");
		material.setType("HASTELLOY");
		material.setCategory("PIPE");
		material.setDimensions("100MM,50MM");
		
		todb = new ArrayList<ExtQuotedMaterials>();
		
		ExtQuotedMaterials qmaterial = new ExtQuotedMaterials();
		
		qmaterial.setItemcode("TEST001");
		qmaterial.setDescription("SOME PIPE");
		qmaterial.setType("HASTELLOY");
		qmaterial.setCategory("PIPE");
		qmaterial.setDimensions("100MM,50MM");
		qmaterial.setProviderId("PROV001");
		qmaterial.setQuantity(100);
		qmaterial.setTotalPrice(580);
		
		todb.add(qmaterial);
		
		qmaterial = new ExtQuotedMaterials();
		
		qmaterial.setItemcode("TEST001");
		qmaterial.setDescription("SOME PIPE");
		qmaterial.setType("HASTELLOY");
		qmaterial.setCategory("PIPE");
		qmaterial.setDimensions("100MM,50MM");
		qmaterial.setProviderId("PROV002");
		qmaterial.setQuantity(100);
		qmaterial.setTotalPrice(210);
		
		todb.add(qmaterial);
		
		qmaterial = new ExtQuotedMaterials();
		
		qmaterial.setItemcode("TEST001");
		qmaterial.setDescription("SOME PIPE");
		qmaterial.setType("HASTELLOY");
		qmaterial.setCategory("PIPE");
		qmaterial.setDimensions("100MM,50MM");
		qmaterial.setProviderId("PROV003");
		qmaterial.setQuantity(100);
		qmaterial.setTotalPrice(127);
		
		todb.add(qmaterial);
		
		qmaterial = new ExtQuotedMaterials();
		
		qmaterial.setItemcode("TEST001");
		qmaterial.setDescription("SOME PIPE");
		qmaterial.setType("HASTELLOY");
		qmaterial.setCategory("PIPE");
		qmaterial.setDimensions("100MM,50MM");
		qmaterial.setProviderId("PROV002");
		qmaterial.setQuantity(100);
		qmaterial.setTotalPrice(150);
		
		todb.add(qmaterial);
		
		qmaterial = new ExtQuotedMaterials();
		
		qmaterial.setItemcode("TEST001");
		qmaterial.setDescription("SOME PIPE");
		qmaterial.setType("HASTELLOY");
		qmaterial.setCategory("PIPE");
		qmaterial.setDimensions("100MM,50MM");
		qmaterial.setProviderId("PROV002");
		qmaterial.setQuantity(100);
		qmaterial.setTotalPrice(310);
		
		todb.add(qmaterial);
		
		ExtQuotedMaterialsController.create(todb);
		
		
	}
	
	@AfterClass
	public static void afterClass() {
		
		for( ExtQuotedMaterials testmat : todb) {			
			ExtQuotedMaterialsController.delete( testmat );
		}
		
	}
	
	@Test
	public void verificationTest() {

		
		double average = QuotesAnalysisSvc.getMaterialPriceAvg("PROVTEST", material);
		
		assertEquals(2.754, average, 0.001);
		
		assertTrue(true);
		
	}
	
	@Test
	public void averageCalcTest() {

		ArrayList<Double> values = new ArrayList<Double>();
		values.add(1.0);
		values.add(2.0);
		values.add(3.0);
		values.add(4.0);
		
		double average = Utilities.calculateAverage(values);
		
		assertEquals(2.5, average, 0.001);
		
	}

}
