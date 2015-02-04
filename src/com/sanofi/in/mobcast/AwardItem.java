package com.sanofi.in.mobcast;

public class AwardItem {
	String awardSum;
	String awardTitle;
	String awardName;
	String imgPath;

	public AwardItem() {
		super();
	}

	public AwardItem(String awardTitle, String awardName, String imgPath,
			String awardSum) {
		super();
		this.awardSum = awardSum;
		this.awardTitle = awardTitle;
		this.awardName = awardName;
		this.imgPath = imgPath;
	}

}
