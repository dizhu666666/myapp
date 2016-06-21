package com.xiakee.xkxsns.ui.adapter;

import java.util.List;

import com.android.util.DeviceInfo;
import com.android.util.ImageUtil;
import com.android.util.SNSAPI;
import com.android.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.Label;
import com.xiakee.xkxsns.bean.SectionData;
import com.xiakee.xkxsns.model.ImageLoader;
import com.xiakee.xkxsns.model.ImageLoader.ImageLoaderListener;
import com.xiakee.xkxsns.ui.activity.PostActivity;
import com.xiakee.xkxsns.ui.activity.TopicTextEditActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class PostTopicAdapter extends CustomBaseAdapter implements OnScrollListener {

	public interface AddSectionHandler {

		Activity getContext();

		void onAddClicked(int position, int type);

		void onDelSection(int position);
	}

	private Handler mHandler;
	private ImageLoader mImgLoader;
	private ListView mListView;
	private AddSectionHandler mSectionHandler;
	protected int mScreenHeight = 0;
	protected int mScreenWidth = 0;
	private List<SectionData> mSectionList;
	private RelativeLayout.LayoutParams Item_llp;
	private RelativeLayout.LayoutParams add_img_llp;
	private int mFirstVisibleItem;
	private int mVisibleItemCount;
	private int mLabelId = 1;

	private final int TYPE_BRAND = 1;
	private final int TYPE_PRODUCTONNAME = 2;
	private final int TYPE_OTHER = 3;
	private boolean isFirstEnterThisActivity = true;

	private ArrayAdapter<String> mBrandValues;
	private boolean getLabels = false;
	private String mCurrentRequest;
	private AutoCompleteTextView editor;

	public PostTopicAdapter(Context context, ListView listView, AddSectionHandler handler) {
		super(context);
		mListView = listView;
		mSectionHandler = handler;
		mScreenWidth = DeviceInfo.scrrenWidth(context);
		mScreenHeight = DeviceInfo.scrrenHeight(context);
		Item_llp = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, mScreenHeight / 3);

		add_img_llp = new RelativeLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mListView.setOnScrollListener(this);
		mImgLoader = ImageLoader.Instance(mContext);
		mHandler = new Handler();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSectionList != null ? mSectionList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mSectionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// must add this method,always forget
	@Override
	public int getItemViewType(int position) {
		return mSectionList.get(position).getDataType() == 0 ? 0 : 1;
	}

	// must add this method,always forget
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	// public void refresh(int latestIndex, List<SectionData> sectionList) {
	// mSectionList = sectionList;
	// mListView.setSelection(latestIndex);
	// notifyDataSetChanged();
	// }

	public void refresh(List<SectionData> sectionList) {
		mSectionList = sectionList;
		isFirstEnterThisActivity = true;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, final View convertView, ViewGroup parent, ViewHolder holder) {
		final SectionData current = mSectionList.get(position);
		final String value = mSectionList.get(position).getmRealValue();
		final String compressedImgPath = mSectionList.get(position).compressedPath;
		final int type = mSectionList.get(position).getDataType();

		if (1 == type) {
			final ImageView imgItem = holder.obtainView(convertView, R.id.item_img_section);
			imgItem.setTag(compressedImgPath);
			setImageView(imgItem, compressedImgPath, position);
			adapteLabel(position, convertView, holder);
			initEditlabelListtener(position, convertView, holder);
			calcDelSectionPosition(position, convertView, holder);
			calcAddSectionPosition(position, convertView, holder);
		} else {
			final TextView textItem = holder.obtainView(convertView, R.id.item_text_section);
			textItem.setHint(StringUtils.getHintValue(mContext, R.string.content_hint));
			textItem.setLayoutParams(Item_llp);
			textItem.setText(value);
			textItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PostActivity activity = (PostActivity) mContext;
					activity.startActivityForResult(new Intent(activity, TopicTextEditActivity.class)
							.putExtra("position", position).putExtra("content", textItem.getText().toString().trim()),PostActivity.EDIT_TEXT);
					activity.overridePendingTransition(0, 0);
				}
			});

			textItem.addTextChangedListener(new TextWatcher() {

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
					current.setmRealValue(s.toString());
				}
			});
			calcDelSectionPosition(position, convertView, holder);
			calcAddSectionPosition(position, convertView, holder);
		}
		return convertView;
	}


	private void setImageView(ImageView imgView, String path, final int position) {
		Bitmap bitmap = mImgLoader.loadBitmapFromMemCache(path);
		if (null != bitmap) {
			imgView.setImageBitmap(bitmap);
		} else {
			imgView.setImageResource(R.drawable.bg_defaut_01);
		}
		imgView.setLayoutParams(Item_llp);
		// imgView.setOnLongClickListener(new OnLongClickListener() {
		//
		// @Override
		// public boolean onLongClick(View v) {
		// // TODO Auto-generated method stub
		// mSectionHandler.onDelSection(position);
		// return false;
		// }
		// });
	}

	private void initEditlabelListtener(final int position, View convertView, ViewHolder holder) {
		final ImageView editTag = (ImageView) holder.obtainView(convertView, R.id.img_tag_edit);
		editTag.setTag(position);
		editTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v instanceof ImageView) {
					// TODO Auto-generated method stub
					final ImageView editView = (ImageView) v;
					final int position = Integer.valueOf(editView.getTag().toString());
				}

				mHandler.post(new Runnable() {
					private LayoutInflater lif = (LayoutInflater) mContext
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

					@SuppressLint("HandlerLeak")
					@Override
					public void run() {
						final Dialog dialog = new Dialog(mContext, R.style.Dialog_FullScreen);
						View editView = lif.inflate(R.layout.view_label_edit, null, false);
						Handler handler = new Handler() {

							@Override
							public void handleMessage(Message msg) {
								dialog.dismiss();
								notifyDataSetChanged();
								super.handleMessage(msg);
							}
						};
						initLabelEditor(dialog, handler, position, editView);
						dialog.setContentView(editView);
						dialog.show();
					}
				});
			}
		});
	}

	private void initLabelEditor(final Dialog dialog, final Handler handler, final int position, View editView) {
		editor = (AutoCompleteTextView) editView.findViewById(R.id.label_input);
		final TextView brand_value = (TextView) editView.findViewById(R.id.label_brand);
		final TextView name_value = (TextView) editView.findViewById(R.id.label_name);
		final TextView other_value = (TextView) editView.findViewById(R.id.label_other);
		final TextView edit_complete = (TextView) editView.findViewById(R.id.edit_complete);
		final TextView cancelButton = (TextView) editView.findViewById(R.id.label_edit_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		brand_value.setSelected(true);
		final Watcher watcher = new Watcher(brand_value);
		editor.setDropDownBackgroundResource(R.drawable.toast_actionsheet_top_normal);
		brand_value.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				brand_value.setSelected(true);
				name_value.setSelected(false);
				other_value.setSelected(false);
				watcher.value = brand_value;
				String brand_text = brand_value.getText().toString();
				if (brand_text == null || brand_text.length() == 0) {
					editor.setText("");
					editor.setHint(R.string.intput_brand);
				} else {
					editor.setText(brand_value.getText());
				}
			}
		});

		name_value.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				brand_value.setSelected(false);
				name_value.setSelected(true);
				other_value.setSelected(false);
				watcher.value = name_value;
				String name_text = name_value.getText().toString();
				if (name_text == null || name_text.length() == 0) {
					editor.setText("");
					editor.setHint(R.string.intput_production_name);
				} else {
					editor.setText(name_value.getText());
				}
			}
		});

		other_value.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				brand_value.setSelected(false);
				name_value.setSelected(false);
				other_value.setSelected(true);
				watcher.value = other_value;
				String other_text = other_value.getText().toString();
				if (other_text == null || other_text.length() == 0) {
					editor.setText("");
					editor.setHint(R.string.intput_other);
				} else {
					editor.setText(other_value.getText());
				}
			}
		});

		edit_complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final SectionData sectionData = mSectionList.get(position);
				for (int i = 0; i < 3; ++i) {
					Label label = new Label();
					label.typeId = i + 1;
					sectionData.labels.add(label);
				}
				sectionData.labels.get(0).title = brand_value.getText().toString();
				sectionData.labels.get(1).title = name_value.getText().toString();
				sectionData.labels.get(2).title = other_value.getText().toString();
				// Message msg = handler.obtainMessage();
				// msg.what = 0;
				handler.sendEmptyMessage(0);
			}
		});
		editor.addTextChangedListener(watcher);
		editor.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				editor.setText("选我选我");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void adapteLabel(final int position, View convertView, ViewHolder holder) {
		final SectionData current = mSectionList.get(position);
		final TextView label = holder.obtainView(convertView, R.id.label_brand);
		final TextView prd_name = holder.obtainView(convertView, R.id.label_production_name);
		final TextView others = holder.obtainView(convertView, R.id.label_others);
		int size = current.labels.size();
		if (0 == size) {
			label.setVisibility(View.GONE);
			prd_name.setVisibility(View.GONE);
			others.setVisibility(View.GONE);
			return;
		}
		for (int i = 0; i < size; ++i) {
			final String title = current.labels.get(i).title;
			if (i == 0) {
				label.setVisibility(title != null ? View.VISIBLE : View.GONE);
				label.setText(title != null ? title : "");
			} else if (i == 1) {
				prd_name.setVisibility(title != null ? View.VISIBLE : View.GONE);
				prd_name.setText(title != null ? title : "");
			} else if (2 == i) {
				others.setVisibility(title != null ? View.VISIBLE : View.GONE);
				others.setText(title != null ? title : "");
			}
		}

	}

	private void calcDelSectionPosition(final int position, View convertView, ViewHolder holder) {

		final ImageView imgDelSectionView = (ImageView) holder.obtainView(convertView, R.id.img_del_section);
		final int height = ImageUtil.getViewHeight(imgDelSectionView);
		// imgDelSectionView.setPadding(-height / 2, -height / 2, 0, 0);
		imgDelSectionView.setTag(position);
		imgDelSectionView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v instanceof ImageView) {
					ImageView imgView = (ImageView) v;
					int position = Integer.valueOf(imgView.getTag().toString());
					mSectionHandler.onDelSection(position);
				}
			}
		});
	}

	private void calcAddSectionPosition(final int position, View convertView, ViewHolder holder) {

		final ImageView imgView = (ImageView) holder.obtainView(convertView, R.id.img_adding_tag);
		final int viewHeight = ImageUtil.getViewHeight(imgView);
		
		add_img_llp = (LayoutParams) imgView.getLayoutParams();

//		add_img_llp.addRule(RelativeLayout.BELOW, resId);
		add_img_llp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		add_img_llp.setMargins(0, -viewHeight / 2, 0, 0);
		imgView.setLayoutParams(add_img_llp);
		imgView.setTag(position);
		imgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v instanceof ImageView) {
					ImageView imgView = (ImageView) v;
					int position = Integer.valueOf(imgView.getTag().toString());
					mSectionHandler.onAddClicked(position, mSectionList.get(position).getDataType());
				}
			}
		});
	}

	/*
	 * private void add2MemCache(String key, Bitmap bitmap) { if (bitmap ==
	 * null) { memCache.put(key, bitmap); } }
	 */
	@Override
	public int itemLayoutId(int position) {
		return mSectionList.get(position).getDataType() == 1 ? R.layout.item_post_topic_img
				: R.layout.item_post_topic_text;
	}

	public void fluchCache() {
		// TODO Auto-generated method stubs
		mImgLoader.fluchCache();
	}

	private class Watcher implements TextWatcher {

		public TextView value;

		public Watcher(TextView value_TV) {
			// TODO Auto-generated constructor stub
			value = value_TV;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			value.setText(s.toString());
			if (s.toString().length() >= 1 && !getLabels) {
				httpGet(SNSAPI.getLabelQuery(s.toString(), 1));
				getLabels = true;
			} else {
				if (0 == s.toString().length()) {
					getLabels = false;
				}
			}
		}
	}

	/**
	 * 为ListView的item加载图片
	 * 
	 * @param firstVisibleItem
	 *            Listview中可见的第一张图片的下标
	 * @param visibleItemCount
	 *            Listview中可见的图片的数量
	 */
	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
		
		
//		int head = mListView.getHeaderViewsCount();
//		int foot = mListView.getFooterViewsCount();
		
		
//		int total = head + foot;
		for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
			// String imagePath = mSectionList.get(i).getmRealValue();
			final String compressedImgPath = mSectionList.get(i).compressedPath;
			Log.e("onImageLoaded", "path: " + compressedImgPath);
			final Bitmap bitmap = mImgLoader.loadBitmapFromLocal(compressedImgPath, new ImageLoaderListener() {

				@Override
				public void onImageLoaded(Bitmap bitmap, String path) {
					// TODO Auto-generated method stub
					final ImageView img = (ImageView) mListView.findViewWithTag(path);
					if (img != null) {
						img.setImageBitmap(bitmap);
					}
					Log.e("onImageLoaded", "path: " + path);
				}
			});
			ImageView view = (ImageView) mListView.findViewWithTag(compressedImgPath);
			if (null != view && null != bitmap) {
				view.setImageBitmap(bitmap);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == SCROLL_STATE_IDLE) {
			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		if (isFirstEnterThisActivity && visibleItemCount > 0) {
			loadBitmaps(firstVisibleItem, visibleItemCount);
			isFirstEnterThisActivity = false;
		}
	}

	public void httpGet(String url) {
		if (mCurrentRequest != null) {
			return; // one request a time
		}
		mCurrentRequest = url;
		// showDialog(PROGRESS_DIALOG);
		// Ion.with(this).load(url).addHeader(arg0, arg1);
		Ion.with(mContext).load(url).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
			@Override
			public void onCompleted(Exception e, JsonObject result) {
				handleResult(e, result);
			}
		});
	}

	private void handleResult(Exception e, JsonObject result) {
		// this.dismissDialog(PROGRESS_DIALOG);
		if (e != null) {
			if ("java.util.concurrent.TimeoutException".equalsIgnoreCase(e.getMessage())) {
				Toast.makeText(mContext, "请检查网络连接！", 1000).show();
			}
		} else if (result != null && !result.isJsonNull()) {
			JsonElement rescode = result.get(SNSAPI.RESULT_CODE);
			if (SNSAPI.RESULT_CODE_ERROR.equals(rescode.getAsString())) {
				JsonElement resmsg = result.get(SNSAPI.RESULT_MESSAGE);
				Toast.makeText(mContext, resmsg.getAsString(), 1000).show();
			} else {
				Labels labels = null;
				Gson gson = new Gson();
				String json = result.toString();
				labels = gson.fromJson(json, Labels.class);
				updateEditor(labels);
			}
		}
		mCurrentRequest = null;
	}

	private void updateEditor(Labels labels) {
		String[] label = new String[labels.labelList.size()];
		for (int i = 0; i < labels.labelList.size(); ++i) {
			label[i] = labels.labelList.get(i).title;
		}
		mBrandValues = new ArrayAdapter<String>(mContext, R.layout.item_autotext, label);
		editor.setAdapter(mBrandValues);
		mBrandValues.notifyDataSetChanged();
	}

	public class Labels {
		public List<Label> labelList;
	}
}