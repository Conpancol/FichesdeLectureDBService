package co.phystech.aosorio.models;

import java.util.List;
import java.util.ArrayList;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("quotes")
public class Quotes {
	
	@Id
    private ObjectId id;
	
	private int internalCode;
	private int externalCode;
	private int revision;
	private String providerCode;
	private String receivedDate;
	private String processedDate;
	private String sentDate;
	private String user;
	private String providerId;
	private String providerName;
	private String contactName;
	private String incoterms;
	private String note;
	private String edt;
		
	@Embedded
	private List<QuotedMaterials> materialList = new ArrayList<QuotedMaterials>();

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
	 * @return the internalCode
	 */
	public int getInternalCode() {
		return internalCode;
	}

	/**
	 * @param internalCode the internalCode to set
	 */
	public void setInternalCode(int internalCode) {
		this.internalCode = internalCode;
	}

	/**
	 * @return the externalCode
	 */
	public int getExternalCode() {
		return externalCode;
	}

	/**
	 * @param externalCode the externalCode to set
	 */
	public void setExternalCode(int externalCode) {
		this.externalCode = externalCode;
	}

	/**
	 * @return the providerCode
	 */
	public String getProviderCode() {
		return providerCode;
	}

	/**
	 * @param providerCode the providerCode to set
	 */
	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	/**
	 * @return the receivedDate
	 */
	public String getReceivedDate() {
		return receivedDate;
	}

	/**
	 * @param receivedDate the receivedDate to set
	 */
	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

	/**
	 * @return the processedDate
	 */
	public String getProcessedDate() {
		return processedDate;
	}

	/**
	 * @param processedDate the processedDate to set
	 */
	public void setProcessedDate(String processedDate) {
		this.processedDate = processedDate;
	}

	/**
	 * @return the sentDate
	 */
	public String getSentDate() {
		return sentDate;
	}

	/**
	 * @param sentDate the sentDate to set
	 */
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
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
	 * @return the materialList
	 */
	public List<QuotedMaterials> getMaterialList() {
		return materialList;
	}

	/**
	 * @param ext the materialList to set
	 */
	public void setMaterialList(List<QuotedMaterials> ext) {
		this.materialList = ext;
	}

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @return the incoterms
	 */
	public String getIncoterms() {
		return incoterms;
	}

	/**
	 * @param incoterms the incoterms to set
	 */
	public void setIncoterms(String incoterms) {
		this.incoterms = incoterms;
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

	/**
	 * @return the edt
	 */
	public String getEdt() {
		return edt;
	}

	/**
	 * @param edt the edt to set
	 */
	public void setEdt(String edt) {
		this.edt = edt;
	}

}
