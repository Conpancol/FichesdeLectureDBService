/**
 * 
 */
package materialsdbsvc;

import static org.junit.Assert.*;

import org.junit.Test;

import co.phystech.aosorio.models.ExtQuotedMaterials;

/**
 * @author AOSORIO
 *
 */
public class ExQuotedMaterialsTest {

	@Test
	public void materialCreationTest() {

		ExtQuotedMaterials material = new ExtQuotedMaterials();
		
		material.setProviderId("CN101");
		material.setProviderName("Chinese provider name");
		
		
		assertEquals(1, 1);

	}

}
