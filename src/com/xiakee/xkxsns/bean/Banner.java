package com.xiakee.xkxsns.bean;

public class Banner {
	private int topicId;
	private String topicDesc;
	private String imgUrl;

	public int getBbsId() {
		return topicId;
	}

	public void setBbsId(int bbsId) {
		this.topicId = bbsId;
	}

	public String getBbsDesc() {
		return topicDesc;
	}

	public void setBbsDesc(String bbsDesc) {
		this.topicDesc = bbsDesc;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}
