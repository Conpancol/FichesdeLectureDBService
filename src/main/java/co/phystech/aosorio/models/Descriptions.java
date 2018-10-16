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
public class Descriptions {
	
	private String locale;
	private String description;
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

}
