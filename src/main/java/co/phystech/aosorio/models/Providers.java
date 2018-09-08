/**
 * 
 */
package co.phystech.aosorio.models;

import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author AOSORIO
 *
 */

@Entity("providers")
public class Providers {
	
	@Id
	private String providerId;
	
	private String providerName; //change to name -> to base clase
	private String category; 
	private String country; // -> to base clase
	private String countryCode; // -> to base clase
	private String city; // -> to base clase
	private String coordinates;  // -> to base clase
	private String providerWeb; //change to webpage
	private String address; // -> to base clase
	private String phone; // -> to base clase
	private String emailAddresses; // -> to base clase
	private String contactNames; // -> to base clase
	private String specialty; 
	
	@Embedded
    private List<Comments> comments;

	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	/**
	 * @return the providerName
	 */
	public String getProviderName() {
		return providerName;
	}

	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the providerWeb
	 */
	public String getProviderWeb() {
		return providerWeb;
	}

	/**
	 * @param providerWeb the providerWeb to set
	 */
	public void setProviderWeb(String providerWeb) {
		this.providerWeb = providerWeb;
	}

	/**
	 * @return the emailAddresses
	 */
	public String getEmailAddresses() {
		return emailAddresses;
	}

	/**
	 * @param emailAddresses the emailAddresses to set
	 */
	public void setEmailAddresses(String emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	/**
	 * @return the contactNames
	 */
	public String getContactNames() {
		return contactNames;
	}

	/**
	 * @param contactNames the contactNames to set
	 */
	public void setContactNames(String contactNames) {
		this.contactNames = contactNames;
	}
	
	/**
	 * @return the specialty
	 */
	public String getSpecialty() {
		return specialty;
	}

	/**
	 * @param specialty the specialty to set
	 */
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the coordinates
	 */
	public String getCoordinates() {
		return coordinates;
	}

	/**
	 * @param coordinates the coordinates to set
	 */
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the comments
	 */
	public List<Comments> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}
	

}
