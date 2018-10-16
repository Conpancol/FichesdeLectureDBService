/**
 * 
 */
package co.phystech.aosorio.models;

import org.mongodb.morphia.annotations.Embedded;

/**
 * @author AOSORIO
 *
 */
@Embedded
public class QuotedMaterials extends ExtMaterials {
	
	
	private double theoreticalWeight;
	private double givenWeight;
	private double unitPrice;
	private double totalPrice;
	private String currency;
	private String countryOrigin;
	private String note;
	private String partNumber;
	private String oldPartNumber;
		
	/**
	 * @return the theoreticalWeight
	 */
	public double getTheoreticalWeight() {
		return theoreticalWeight;
	}
	/**
	 * @param d the theoreticalWeight to set
	 */
	public void setTheoreticalWeight(double d) {
		this.theoreticalWeight = d;
	}
	/**
	 * @return the givenWeight
	 */
	public double getGivenWeight() {
		return givenWeight;
	}
	/**
	 * @param d the givenWeight to set
	 */
	public void setGivenWeight(double d) {
		this.givenWeight = d;
	}
	/**
	 * @return the unitPrice
	 */
	public double getUnitPrice() {
		return unitPrice;
	}
	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	/**
	 * @return the totalPrice
	 */
	public double getTotalPrice() {
		return totalPrice;
	}
	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @return the countryOrigin
	 */
	public String getCountryOrigin() {
		return countryOrigin;
	}
	/**
	 * @param countryOrigin the countryOrigin to set
	 */
	public void setCountryOrigin(String countryOrigin) {
		this.countryOrigin = countryOrigin;
	}
	/**
	 * @return the partNumber
	 */
	public String getPartNumber() {
		return partNumber;
	}
	/**
	 * @param partNumber the partNumber to set
	 */
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	/**
	 * @return the oldPartNumber
	 */
	public String getOldPartNumber() {
		return oldPartNumber;
	}
	/**
	 * @param oldPartNumber the oldPartNumber to set
	 */
	public void setOldPartNumber(String oldPartNumber) {
		this.oldPartNumber = oldPartNumber;
	}
	
	
}
