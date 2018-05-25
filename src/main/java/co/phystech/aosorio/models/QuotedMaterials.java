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
	
	
	private float theoreticalWeight;
	private float givenWeight;
	private float unitPrice;
	private float totalPrice;
	/**
	 * @return the theoreticalWeight
	 */
	public float getTheoreticalWeight() {
		return theoreticalWeight;
	}
	/**
	 * @param theoreticalWeight the theoreticalWeight to set
	 */
	public void setTheoreticalWeight(float theoreticalWeight) {
		this.theoreticalWeight = theoreticalWeight;
	}
	/**
	 * @return the givenWeight
	 */
	public float getGivenWeight() {
		return givenWeight;
	}
	/**
	 * @param givenWeight the givenWeight to set
	 */
	public void setGivenWeight(float givenWeight) {
		this.givenWeight = givenWeight;
	}
	/**
	 * @return the unitPrice
	 */
	public float getUnitPrice() {
		return unitPrice;
	}
	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}
	/**
	 * @return the totalPrice
	 */
	public float getTotalPrice() {
		return totalPrice;
	}
	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
}
