/**
 * 
 */
package co.phystech.aosorio.models;

import org.mongodb.morphia.annotations.Entity;

/**
 * @author AOSORIO
 *
 */
@Entity("schedules")
public class PipeSchedules {
	
    private String schedule;
    private String nps;
    private String od;
    private double wt;
    private double npsMM;
    private double odMM;
    private double wtMM;
    private String code;
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
	 * @return the nps
	 */
	public String getNps() {
		return nps;
	}
	/**
	 * @param nps the nps to set
	 */
	public void setNps(String nps) {
		this.nps = nps;
	}
	/**
	 * @return the od
	 */
	public String getOd() {
		return od;
	}
	/**
	 * @param od the od to set
	 */
	public void setOd(String od) {
		this.od = od;
	}
	/**
	 * @return the wt
	 */
	public double getWt() {
		return wt;
	}
	/**
	 * @param wt the wt to set
	 */
	public void setWt(double wt) {
		this.wt = wt;
	}
	/**
	 * @return the npsMM
	 */
	public double getNpsMM() {
		return npsMM;
	}
	/**
	 * @param npsMM the npsMM to set
	 */
	public void setNpsMM(double npsMM) {
		this.npsMM = npsMM;
	}
	/**
	 * @return the odMM
	 */
	public double getOdMM() {
		return odMM;
	}
	/**
	 * @param odMM the odMM to set
	 */
	public void setOdMM(double odMM) {
		this.odMM = odMM;
	}
	/**
	 * @return the wtMM
	 */
	public double getWtMM() {
		return wtMM;
	}
	/**
	 * @param wtMM the wtMM to set
	 */
	public void setWtMM(double wtMM) {
		this.wtMM = wtMM;
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
        
	
	//... Inner diameter
	public double getIdMM() {
		return odMM - (2.0*wtMM);
	}
	
}
