package com.xiakee.xkxsns.bean;

public class TopicDetail {
	// bbsList 数组
	private String topicTime;// 发帖时间距当前时间的时间差
	private String topicDesc;
	private int commentCount;
	private int goodCount;
	private int imgCount;
	private String imgUrl1;

	private String imgUrl2;
	private String imgUrl3;
	public int topicId;
	private int readCount;
	private String title;
	private int userId;
	private String photo;
	private String userName;
	private int goodStatus; //点赞状态
	private String postTime; //发帖时间
	private int sex;
	private int lv;

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

	public String getPostTime() {
		return postTime;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public void setTopicTime(String topicTime) {
		this.topicTime = topicTime;
	}

	public void setTopicDesc(String topicDesc) {
		this.topicDesc = topicDesc;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public void setPostTime(String postTime) {
		this.postTime = postTime;
	}

	public int getGoodStatus() {
		return goodStatus;
	}

	public void setGoodStatus(int goodStatus) {
		this.goodStatus = goodStatus;
	}

	public String getTopicTime() {
		return topicTime;
	}

	public void setAddTime(String topicTime) {
		this.topicTime = topicTime;
	}

	public String getTopicDesc() {
		return topicDesc;
	}

	public void setBbsDesc(String bbsDesc) {
		this.topicDesc = bbsDesc;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getGoodCount() {
		return goodCount;
	}

	public void setGoodCount(int goodCount) {
		this.goodCount = goodCount;
	}

	public int getImgCount() {
		return imgCount;
	}

	public void setImgCount(int imgCount) {
		this.imgCount = imgCount;
	}

	public String getImgUrl() {
		return imgUrl1;
	}

	public void setImgUrl1(String imgUrl) {
		this.imgUrl1 = imgUrl;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setBbsId(int bbsId) {
		this.topicId = bbsId;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getImgUrl2() {
		return imgUrl2;
	}

	public void setImgUrlTwo(String imgUrlTwo) {
		this.imgUrl2 = imgUrlTwo;
	}

	public String getImgUrl3() {
		return imgUrl3;
	}

	public void setImgUrlThree(String imgUrlThree) {
		this.imgUrl3 = imgUrlThree;
	}

}
