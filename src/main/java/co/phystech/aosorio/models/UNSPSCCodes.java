/**
 * 
 */
package co.phystech.aosorio.models;

import org.mongodb.morphia.annotations.Entity;

/**
 * @author AOSORIO
 *
 */
@Entity("unspsccodes")
public class UNSPSCCodes {
	
    private String segment; //SEGMENT
    private String family; //FAMILY
    private String category; //CLASS
    private String commodity; //COMMODITY
    private String code;
    private String locale;
    private String description;
    private String segment_description;
    private String family_description;
    private String category_description;
	/**
	 * @return the segment
	 */
	public String getSegment() {
		return segment;
	}
	/**
	 * @param segment the segment to set
	 */
	public void setSegment(String segment) {
		this.segment = segment;
	}
	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}
	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
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
	 * @return the id
	 */
	public String getCommodity() {
		return commodity;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String commodity) {
		this.commodity = commodity;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}
	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the segment_description
	 */
	public String getSegment_description() {
		return segment_description;
	}
	/**
	 * @param segment_description the segment_description to set
	 */
	public void setSegment_description(String segment_description) {
		this.segment_description = segment_description;
	}
	/**
	 * @return the family_description
	 */
	public String getFamily_description() {
		return family_description;
	}
	/**
	 * @param family_description the family_description to set
	 */
	public void setFamily_description(String family_description) {
		this.family_description = family_description;
	}
	/**
	 * @return the category_description
	 */
	public String getCategory_description() {
		return category_description;
	}
	/**
	 * @param category_description the category_description to set
	 */
	public void setCategory_description(String category_description) {
		this.category_description = category_description;
	}
	
    

}
