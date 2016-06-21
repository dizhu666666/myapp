package com.xiakee.xkxsns.model;

import java.util.ArrayList;
import java.util.List;

import com.android.iosdialog.IosDialog;
import com.android.iosdialog.IosDialog.OnMyItemClickListner;
import com.android.iosdialog.SheetItem;
import com.android.photo.util.Bimp;
import com.android.photocropper.CropHelper;
import com.android.photocropper.CropParams;
import com.android.util.ActivityLauncher;
import com.android.util.DeviceInfo;
import com.android.util.ImageUtil;
import com.android.util.StringUtils;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.SectionData;
import com.xiakee.xkxsns.ui.activity.PhotosSeclectActivity;
import com.xiakee.xkxsns.ui.activity.PostActivity;
import com.xiakee.xkxsns.ui.activity.TopicTextEditActivity;
import com.xiakee.xkxsns.ui.adapter.PostTopicAdapter;
import com.xiakee.xkxsns.ui.adapter.PostTopicAdapter.AddSectionHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//import android.widget.Toast;

public class PostDataManager implements OnItemClickListener, OnMyItemClickListner, AddSectionHandler {

	private static int INDEX_TITLE = -1;// means title
	private int currentIndex = INDEX_TITLE;
	private Context mContext;
	private View mParent;
	private ListView mListView;
	private PostTopicAdapter mAdapter;
	public List<SectionData> mSectionList;
	public EditText mTitleEtitor;

	private Handler mHandler;

	public final int minLength = 4;

	public PostDataManager(Context context, View parent, Handler handler) {
		this.mContext = context;
		this.mParent = parent;
		this.mHandler = handler;
		mSectionList = new ArrayList<SectionData>();
		init(parent);
	}

	public List<String> getSelectedOriginPics() {
		// Bimp.tempSelectBitmap.get(currentIndex)
		List<String> selectedPics = new ArrayList<String>();
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); ++i) {
			selectedPics.add(Bimp.tempSelectBitmap.get(i).imagePath);
		}
		return selectedPics;
	}

	public List<String> getSelectedCompressedPics() {
		// Bimp.tempSelectBitmap.get(currentIndex)
		List<String> selectedPics = new ArrayList<String>();
		for (int i = 0; i < mSectionList.size(); ++i) {
			if (mSectionList.get(i).getDataType() == 1) {
				selectedPics.add(mSectionList.get(i).compressedPath);
			}
		}
		return selectedPics;
	}

	public boolean checkAddedAlready(String path) {
		for (int i = 0; i < mSectionList.size(); ++i) {
			if (path.equals(mSectionList.get(i).getmRealValue())) {
				return true;
			}
		}
		return false;
	}

	public boolean addTail(SectionData data) {
		return mSectionList.add(data);
	}

	public void addAhead(SectionData data) {
		mSectionList.add(0, data);
	}

	public void insertSection(int index, SectionData data) {
		mSectionList.add(index, data);
	}

	public void remove(int index) {
		mSectionList.remove(index);
	}

	public void remove(SectionData data) {
		mSectionList.remove(data);
	}

	public void refresh(int currentindex) {
		mAdapter.refresh(mSectionList);
		mListView.setSelection(currentindex);
	}

	public void textRefresh(int position, String text) {
        mSectionList.get(position).setmRealValue(text);
        refresh(position);
    }
	
	public int getCurrentIndex() {
		return currentIndex;
	}

	public List<SectionData> getSectionsList() {
		return mSectionList;
	}

	public String getTitle() {
		return mTitleEtitor.getText().toString();
	}

	private void initListView(View parent) {
		mListView = (ListView) parent.findViewById(R.id.post_editorlist);
//		addInvisible();
		mAdapter = new PostTopicAdapter(mContext, mListView, this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}
	

	private void addInvisible() {
		
		final LayoutInflater lif = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view =   lif.inflate(R.layout.item_transparent, null, false);
		int height = view.getHeight();
		view.setMinimumHeight(1500);
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(1,1500);
		view.setLayoutParams(llp);
		height = view.getHeight();
		mListView.addHeaderView(view);
//		mListView.setSelection(0);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}

	private void init(View parent) {
		initArticleTitle(parent);
		initListView(parent);
	}

	private void iosDialogShow() {
		final IosDialog iosDialog = new IosDialog(mContext, this);
		List<SheetItem> listSheetItems = new ArrayList<SheetItem>();
		listSheetItems.add(new SheetItem(mContext.getResources().getString(R.string.take_pic), 1));
		listSheetItems.add(new SheetItem(mContext.getResources().getString(R.string.select_from_album), 2));
		iosDialog.setSheetItems(listSheetItems);
		iosDialog.show();
	}

	private void initTitleEditor(View view) {
		mTitleEtitor = (EditText) view.findViewById(R.id.title_editor);
		mTitleEtitor.setHint(StringUtils.getHintValue(mContext, R.string.topic_title_hint)); // 一定要进行转换,否则属性会消失
		mTitleEtitor.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.toString().length() >= 4) {
					Message msg = mHandler.obtainMessage();
					msg.what = PostActivity.ENABLE_PREVIEW_POST;
					mHandler.sendMessage(msg);
				} else {
					Message msg = mHandler.obtainMessage();
					msg.what = PostActivity.DISABLE_PREVIEW_POST;
					mHandler.sendMessage(msg);
				}
			}
		});
	}

	private void initArticleTitle(View rootView) {

		initTitleEditor(rootView);
		final ImageView imgView = (ImageView) rootView.findViewById(R.id.img_adding_tag);
		final int viewHeight = ImageUtil.getViewHeight(imgView);
		LinearLayout.LayoutParams img_llp = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,

				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		img_llp.setMargins(0, -viewHeight / 2, 0, 0);
		img_llp.gravity = Gravity.CENTER;
		imgView.setLayoutParams(img_llp);
		imgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				currentIndex = INDEX_TITLE;
				if (0 < mSectionList.size()) {
					if (1 != mSectionList.get(0).getDataType()) {
						// text
						iosDialogShow();
						return;
					}
				}
				sectionTypeSelector();
			}
		});

		final TextView titleText = (TextView) rootView.findViewById(R.id.title_des);
		LinearLayout.LayoutParams title_llp = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		title_llp.setMargins(0, -viewHeight / 2, 0, 0);
		title_llp.gravity = Gravity.LEFT;
		titleText.setLayoutParams(title_llp);
	}

	// custom dialog
	private void sectionTypeSelector() {
		final Dialog dialog = new Dialog(mContext, R.style.FullHeightDialog);
		final View view = LayoutInflater.from(mContext).inflate(R.layout.view_dialog_add_section, null);
		final TextView btnAddText = (TextView) view.findViewById(R.id.section_add_text);
		btnAddText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(mContext, "add a text", 1000).show();
				SectionData sd = new SectionData();
				sd.setSectionType(0);
				// must add 1
				mSectionList.add(currentIndex + 1, sd);
				mAdapter.refresh(mSectionList);
				
				{
					//added by william
					PostActivity activity = (PostActivity) mContext;
					activity.startActivityForResult(new Intent(activity, TopicTextEditActivity.class)
							.putExtra("position", currentIndex+1).putExtra("content", ""),PostActivity.EDIT_TEXT);
					activity.overridePendingTransition(0, 0);
				}
				dialog.dismiss();
			}
		});
		final TextView btnAddPic = (TextView) view.findViewById(R.id.section_add_pic);
		btnAddPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				iosDialogShow();
				dialog.dismiss();
			}
		});
		// set up the custom view width.
		view.setMinimumWidth((int) (0.8 * DeviceInfo.scrrenWidth(mContext)));
		dialog.setContentView(view);
		dialog.show();
	}

	@Override
	public void onClickItem(int which) {
		// TODO Auto-generated method stub
		switch (which) {
		case 1: {
			CropParams mCropParams = calCCropParams();

			Message msg = mHandler.obtainMessage();
			msg.what = PostActivity.GET_CropParams;
			msg.obj = mCropParams;
			mHandler.sendMessage(msg);
			Intent intentTakePic = CropHelper.buildCaptureIntent(mCropParams.uri);
			((Activity) mContext).startActivityForResult(intentTakePic, CropHelper.REQUEST_CAMERA);
			break;
		}
		case 2:
			Intent intent = new Intent(mContext, PhotosSeclectActivity.class);
			intent.putExtra(PostActivity.CURRENT_INDEX, currentIndex);
			ActivityLauncher.startPhotosSeclectActivity(((PostActivity) mContext), intent,
					PostActivity.REQUEST_PHOTO_SELECT);
			break;

		default:
			break;
		}
	}

	@Override
	public Activity getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAddClicked(int position, int type) {
		// TODO Auto-generated method stub
		currentIndex = position;
		if (1 == type) {
			if (position < mSectionList.size() - 1) { // 并非最后一个item，并且当前的是图片，则检查下一个是什么
				if (mSectionList.get(position + 1).getDataType() == 0) {
					iosDialogShow();
					return;
				}
			}
			sectionTypeSelector();

		} else {
			iosDialogShow();
		}

	}

	@Override
	public void onDelSection(int position) {
		// TODO Auto-generated method stub
		removeTempSelected(position);
		mSectionList.remove(position);
		refresh(position);
	}

	public void fluchCache() {
		mAdapter.fluchCache();
	}

	public void clear() {
		mSectionList.clear();
	}

	private void removeTempSelected(int position) {
		final String imgPath = mSectionList.get(position).getmRealValue();
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); ++i) {
			if (imgPath.equals(Bimp.tempSelectBitmap.get(i).imagePath)) {
				Bimp.tempSelectBitmap.get(i).isUsed = false;
				Bimp.tempSelectBitmap.remove(i);
				return;
			}
		}
	}

	private CropParams calCCropParams() {
		CropParams mCropParams = new CropParams();
		final int width = DeviceInfo.scrrenWidth(mContext);
		final int height = DeviceInfo.scrrenHeight(mContext);
		mCropParams.aspectX = 1;
		mCropParams.aspectY = 1;
		mCropParams.outputX = width;
		mCropParams.outputY = height;
		return mCropParams;
	}

}
