package materialsdbsvc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
//import org.mongodb.morphia.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.controllers.ProvidersController;
import co.phystech.aosorio.models.Comments;
import co.phystech.aosorio.models.Providers;

public class ProvidersTest {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(ProvidersTest.class);
	
	private static final String providerId = "MP9001";
	private static final String providerName = "AKANE INC";
	private static final String category = "materials";
	private static final String country = "JAPAN";
	private static final String city = "TOKIO";
	private static final String coordinates = "0.00, 0.00";
	private static final String providerWeb = "www.akaneinc.com";
	private static final String address = "1234 Road, ZIP101";
	private static final String phone = "+1 123467890";	
	private static final String emailAddresses = "email@akaneinc.com";
	private static final String contactNames = "Ms Akane";
	private static final String specialty = "High tech robotics";
	
	@Test
	public void creation() {
	
		Providers provider = new Providers();
		
		provider.setProviderId(providerId);
		provider.setProviderName(providerName);
		provider.setCategory(category.toUpperCase());
		provider.setCountry(country);
		provider.setCountryCode("");
		provider.setCity(city);
		provider.setCoordinates(coordinates);
		provider.setProviderWeb(providerWeb);
		provider.setAddress(address);
		provider.setPhone(phone);
		provider.setEmailAddresses(emailAddresses);
		provider.setContactNames(contactNames);
		provider.setSpecialty(specialty);
		
		Comments acomment = new Comments();
		acomment.setDate("01/01/2100");
		acomment.setIssuer("aosorio");
		acomment.setText("No problems so far. Fast responses.");
		
		List<Comments> comments = new ArrayList<Comments>();
		comments.add(acomment);
		
		provider.setComments(comments);
		
		slf4jLogger.info("Adding new provider");
		
		ProvidersController.create(provider);			
		Providers testProvider = ProvidersController.findOneByName(provider);
		
		assertEquals(testProvider.getProviderName(),providerName);
		
		ProvidersController.delete(provider);
		
	}

}
