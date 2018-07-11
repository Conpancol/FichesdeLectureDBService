/**
 * 
 */
package co.phystech.aosorio.models;

import org.mongodb.morphia.annotations.Entity;

/**
 * @author AOSORIO
 *
 */
@Entity("fittings")
public class Fittings {
		
	String fittingType;
    String category;
    String standards;
    String pipeSize;
    String schedule;
    String dimensions;
    double weight;
    
	/**
	 * @return the fittingType
	 */
	public String getFittingType() {
		return fittingType;
	}
	/**
	 * @param fittingType the fittingType to set
	 */
	public void setFittingType(String fittingType) {
		this.fittingType = fittingType;
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
	 * @return the standards
	 */
	public String getStandards() {
		return standards;
	}
	/**
	 * @param standards the standards to set
	 */
	public void setStandards(String standards) {
		this.standards = standards;
	}
	/**
	 * @return the pipeSize
	 */
	public String getPipeSize() {
		return pipeSize;
	}
	/**
	 * @param pipeSize the pipeSize to set
	 */
	public void setPipeSize(String pipeSize) {
		this.pipeSize = pipeSize;
	}
	/**
	 * @return the schedule
	 */
	public String getSchedule() {
		return schedule;
	}
	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	/**
	 * @return the dimensions
	 */
	public String getDimensions() {
		return dimensions;
	}
	/**
	 * @param dimensions the dimensions to set
	 */
	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}
	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
    
    
}
