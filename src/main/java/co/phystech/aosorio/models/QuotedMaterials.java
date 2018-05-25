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
	
	
}
