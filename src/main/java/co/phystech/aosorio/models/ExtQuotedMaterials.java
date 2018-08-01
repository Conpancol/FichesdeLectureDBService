/**
 * 
 */
package co.phystech.aosorio.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author AOSORIO
 *
 */

@Entity("qmaterials")
public class ExtQuotedMaterials extends QuotedMaterials {

	@Id
	private ObjectId id;

	private String providerId;
	private String providerName;
	private String updateDate;
	private int projectId;
	private int revision;
	private double usdTRM;

	public ExtQuotedMaterials() {
		
	}
	
	public ExtQuotedMaterials(QuotedMaterials material) {
		
		this.setItemcode(material.getItemcode());
		this.setDescription(material.getDescription());
		this.setDimensions(material.getDimensions());
		this.setType(material.getType());
		this.setCategory(material.getCategory());
		this.setOrderNumber(material.getOrderNumber());
		this.setUnit(material.getUnit());
		this.setQuantity(material.getQuantity());
		this.setTheoreticalWeight(material.getTheoreticalWeight());
		this.setGivenWeight(material.getGivenWeight());
		this.setUnitPrice(material.getUnitPrice());		
		this.setTotalPrice(material.getTotalPrice());
		this.setCurrency(material.getCurrency());
		this.setCountryOrigin(material.getCountryOrigin());
		this.setNote(material.getNote());
				
	}

	/**
	 * @return the id
	 */
	public ObjectId getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(ObjectId id) {
		this.id = id;
	}

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
	 * @return the updateDate
	 */
	public String getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return the usdTRM
	 */
	public double getUsdTRM() {
		return usdTRM;
	}

	/**
	 * @param usdTRM the usdTRM to set
	 */
	public void setUsdTRM(double usdTRM) {
		this.usdTRM = usdTRM;
	}

	/**
	 * @return the projectId
	 */
	public int getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the revision
	 */
	public int getRevision() {
		return revision;
	}

	/**
	 * @param revision the revision to set
	 */
	public void setRevision(int revision) {
		this.revision = revision;
	}
	
}
