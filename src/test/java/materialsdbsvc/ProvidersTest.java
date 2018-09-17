package materialsdbsvc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
//import org.mongodb.morphia.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.controllers.ProvidersController;
import co.phystech.aosorio.models.Comments;
import co.phystech.aosorio.models.Providers;
import co.phystech.aosorio.services.Utilities;

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
	private static final String taxId = "1234567890";
	private static final String bank = "ABN AMRO";
	private static final String iban = "1234567890";
	private static final boolean hasdt = false;

	@BeforeClass
	public static void beforeClass() {

		Providers provider = new Providers();

		provider.setProviderId(providerId);
		provider.setName(providerName);
		provider.setCategory(category.toUpperCase());
		provider.setCountry(country);
		provider.setCountryCode(Utilities.getCountryCode(country));
		provider.setCity(city);
		provider.setCoordinates(coordinates);
		provider.setWebpage(providerWeb);
		provider.setAddress(address);
		provider.setPhone(phone);
		provider.setEmailAddresses(emailAddresses);
		provider.setContactNames(contactNames);
		provider.setSpecialty(specialty);
		provider.setTaxId(taxId);
		provider.setBank(bank);
		provider.setIban(iban);
		provider.setHasDataProtection(hasdt);

		Comments acomment = new Comments();
		acomment.setDate("01/01/2100");
		acomment.setIssuer("aosorio");
		acomment.setText("No problems so far. Fast responses.");

		List<Comments> comments = new ArrayList<Comments>();
		comments.add(acomment);

		provider.setComments(comments);

		slf4jLogger.info("Adding new provider");

		ProvidersController.create(provider);
		
		slf4jLogger.info("size before adding comment " + String.valueOf(provider.getComments().size()));

	}

	@AfterClass
	public static void afterClass() {

		Providers provider = ProvidersController.read(providerId);

		ProvidersController.delete(provider);

	}

	@Test
	public void creation() {

		Providers provider = ProvidersController.read(providerId);

		Providers testProvider = ProvidersController.findOneByName(provider);

		assertEquals(testProvider.getName(), providerName);

	}

	@Test
	public void addCommentTest() {

		Providers testProvider = ProvidersController.read(providerId);

		Comments comment = new Comments();

		comment.setDate("01/01/1001");
		comment.setIssuer("aosorio");
		comment.setText("This is a test comment. Please do not delete");

		ProvidersController.addComment(providerId, comment);
		
		testProvider = ProvidersController.read(providerId);
		
		slf4jLogger.info("size after adding comment " + String.valueOf(testProvider.getComments().size()));

		assertEquals(2, testProvider.getComments().size());

	}

}
