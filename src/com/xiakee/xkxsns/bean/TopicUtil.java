package com.xiakee.xkxsns.bean;

import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class TopicUtil {

	// private JsonObject dataJson;
	// private static volatile Topic sTopic;
	private static final String SP_KEY_TOPIC = "topic";
	//
	public String title;
	public int userId;
	public int typeId;
	public List<SectionData> contents;

	private TopicUtil() {

	}

	public static boolean checkTopicExist(Context context, int typeId) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.contains(typeId + SP_KEY_TOPIC);
	}

	public static void saveTopic(Context context, String jsonData, int typeId) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor = sp.edit();
		// final String strJson = data.getAsString();
		// int typeId = data.get("typeId").getAsInt();
		editor.putString(typeId + SP_KEY_TOPIC, jsonData);
		editor.commit();
		/*
		 * synchronized (Topic.class) { sTopic = null; }
		 */
	}

	public static TopicUtil loadTopic(Context context, int typeId) {
		TopicUtil topic = null;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Gson gson = new Gson();
		String json = sp.getString(typeId + SP_KEY_TOPIC, null);
		if (json != null) {
			topic = gson.fromJson(json, TopicUtil.class);
		}
		return topic;
	}

	public static TopicUtil loadTopic(Context context, String jsonStr) {
		TopicUtil topic = null;
		// SharedPreferences sp =
		// PreferenceManager.getDefaultSharedPreferences(context);
		Gson gson = new Gson();
		// String json = sp.getString(typeId + SP_KEY_TOPIC, null);
		topic = gson.fromJson(jsonStr, TopicUtil.class);
		return topic;
	}

	public static void removewTopic(Context context, int typeId) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.remove(typeId + SP_KEY_TOPIC);
		editor.commit();
		/*
		 * synchronized (Topic.class) { sTopic = null; }
		 */
	}

	/*
	 * private static void saveTopic(Context context, Topic topic) {
	 * SharedPreferences sp =
	 * PreferenceManager.getDefaultSharedPreferences(context); final Editor
	 * editor = sp.edit(); Gson gson = new Gson(); String json =
	 * gson.toJson(topic); editor.putString(SP_KEY_TOPIC, json);
	 * editor.commit(); synchronized (Topic.class) { sTopic = null; } }
	 */

}
