package edu.usc.csci587.icampusevent.objects;

public class Category {

	private long CATEGORY_ID;
	private String CATEGORY_NAME;
	private String CATEGORY_DESCRIPTION;

	public long getCATEGORY_ID() {
		return CATEGORY_ID;
	}

	public String getCATEGORY_NAME() {
		return CATEGORY_NAME;
	}

	public String getCATEGORY_DESCRIPTION() {
		return CATEGORY_DESCRIPTION;
	}

	public Category(long CATEGORY_ID, String CATEGORY_NAME, String CATEGORY_DESCRIPTION) {
		super();
		this.CATEGORY_ID = CATEGORY_ID;
		this.CATEGORY_NAME = CATEGORY_NAME;
		this.CATEGORY_DESCRIPTION = CATEGORY_DESCRIPTION;
	}

}
