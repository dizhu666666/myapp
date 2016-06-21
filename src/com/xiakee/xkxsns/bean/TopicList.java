package com.xiakee.xkxsns.bean;

import java.util.List;

public class TopicList {
	// private TopicDetail[] topicList;
	private List<TopicDetail> topicList;

	public List<TopicDetail> getTopicList() {
		return topicList;
	}

	// public void setTopicList(List<TopicDetail> topicList) {
	// this.topicList = topicList;
	// }

	public List<TopicDetail> addMoreTopicList(List<TopicDetail> topicList) {
		this.topicList.addAll(topicList);
		return this.topicList;
	}

	public List<TopicDetail> addLatestTopicList(List<TopicDetail> topicList) {
		this.topicList.addAll(0, topicList);
		return this.topicList;
	}
}