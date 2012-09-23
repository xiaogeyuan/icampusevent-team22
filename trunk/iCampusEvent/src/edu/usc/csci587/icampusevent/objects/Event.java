/**
 * 
 */
package edu.usc.csci587.icampusevent.objects;

import java.sql.Date;

import oracle.spatial.geometry.JGeometry;


public class Event {
	
	private String CATEGORY_NAME;
	private String CATEGORY_DESCRIPTION;
	
	private long EVENT_ID;
	private String EVENT_NAME;
	private Date START_DATE;
	private Date END_DATE;
	private String EVENT_DESCRIPTION;
	private String IMAGE_URL;
	private String LINK;
	private JGeometry LOCATION;
	
	public String getCATEGORY_NAME() {
		return CATEGORY_NAME;
	}
	public String getCATEGORY_DESCRIPTION() {
		return CATEGORY_DESCRIPTION;
	}
	public long getEVENT_ID() {
		return EVENT_ID;
	}
	public String getEVENT_NAME() {
		return EVENT_NAME;
	}
	public Date getSTART_DATE() {
		return START_DATE;
	}
	public Date getEND_DATE() {
		return END_DATE;
	}
	public String getEVENT_DESCRIPTION() {
		return EVENT_DESCRIPTION;
	}
	public String getIMAGE_URL() {
		return IMAGE_URL;
	}
	public String getLINK() {
		return LINK;
	}
	public JGeometry getLOCATION() {
		return LOCATION;
	}
	
	public Event(String CATEGORY_NAME, String CATEGORY_DESCRIPTION, long EVENT_ID, String EVENT_NAME, Date START_DATE, Date END_DATE,
			String EVENT_DESCRIPTION, String IMAGE_URL, String LINK, JGeometry LOCATION) {
		super();
		this.CATEGORY_NAME = CATEGORY_NAME;
		this.CATEGORY_DESCRIPTION = CATEGORY_DESCRIPTION;
		this.EVENT_ID = EVENT_ID;
		this.EVENT_NAME = EVENT_NAME;
		this.START_DATE = START_DATE;
		this.END_DATE = END_DATE;
		this.EVENT_DESCRIPTION = EVENT_DESCRIPTION;
		this.IMAGE_URL = IMAGE_URL;
		this.LINK = LINK;
		this.LOCATION = LOCATION;
	}
	
	
}
