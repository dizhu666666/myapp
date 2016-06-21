package com.xiakee.xkxsns.bean;

import java.util.ArrayList;
import java.util.List;

public class SectionData {

	private int type;
	public String text;// text or pic path
	private int id;
	public int sort;
	public String compressedPath;
	public List<Label> labels;
	public int xloc;
	public int yloc;

	public SectionData() {
		text = new String();
		labels = new ArrayList<Label>();
	}

	public int getDataType() {
		return type;
	}

	public void setSectionType(int type) {
		this.type = type;
	}

	public String getmRealValue() {
		return text;
	}

	public void setmRealValue(String mRealValue) {
		this.text = mRealValue;
	}

}
