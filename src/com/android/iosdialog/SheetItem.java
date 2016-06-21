package com.android.iosdialog;

public class SheetItem {

	private String strItemName;
	private int mItem_index;// 对每个item定义一个code 用来回调的时候看你点击的是哪一个item;;;

	public SheetItem() {
		super();
	}

	public SheetItem(String strItemName, int iTEM_CODE) {
		super();
		this.strItemName = strItemName;
		mItem_index = iTEM_CODE;
	}
	
	

	public int getITEM_CODE() {
		return mItem_index;
	}

	public String getStrItemName() {
		return strItemName;
	}

	public void setITEM_CODE(int iTEM_CODE) {
		mItem_index = iTEM_CODE;
	}

	public void setStrItemName(String strItemName) {
		this.strItemName = strItemName;
	}

	@Override
	public String toString() {
		return "SheetItem [strItemName=" + strItemName + ", ITEM_CODE="
				+ mItem_index + "]";
	}
}
