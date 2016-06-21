package com.xiakee.xkxsns.ui.activity;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.photo.util.Bimp;
import com.android.photocropper.CropParams;
import com.android.util.ActivityLauncher;
import com.android.util.DeviceInfo;
import com.android.util.FileUtils;
import com.android.util.ImageUtil;
import com.android.util.SNSAPI;
import com.android.util.StringUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.builder.Builders.Any.B;
import com.xiakee.xkxsns.R;
import com.xiakee.xkxsns.bean.SectionData;
import com.xiakee.xkxsns.bean.TopicUtil;
import com.xiakee.xkxsns.global.GlobalApplication;
import com.xiakee.xkxsns.model.PostDataManager;
import com.xiakee.xkxsns.ui.view.LoadingProgressDialog;
import com.xiakee.xkxsns.util.LogUtils;
import com.xiakee.xkxsns.util.UserManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 编辑发帖页面activity
 *
 * @author william
 * @version 2015年10月25日 下午11:48:34
 */
public class PostActivity extends BaseActivity {

	private View mRootView;
	private PostDataManager mManager;
	private TitleBar mTitleBar;
	private TextView mPostAction;
	private TextView mPreviewAction;
	private CropParams mCropParams;
	private Handler mHandler;
	private int typeId;
	private final int TAKE_PICTURE = 0x000001;
	public final static String CURRENT_INDEX = "currentIndex";
	public final static int REQUEST_PHOTO_SELECT = 12;
	public final static int DISABLE_PREVIEW_POST = -1;
	public final static int ENABLE_PREVIEW_POST = 0;
	public final static int GET_CropParams = 1;
	public final static int CANCEL_UPLOADING = 8;
	public final int UPDATE_LIST = 2;
	public final int UPDATE_UPLOADING_PROGRESS = 7;
	public final static int PREVIEW_ACTIVITY_REQUEST_CODE = 125;
	public final static int PREVIEW_RETURN2EDIT = -1;
	public final static int RREVIEW_POST = -2;
    public static final int EDIT_TEXT = 3213;
	private LoadingProgressDialog mProgress;
	// private Callback.Cancelable mCanCelable;

	// BaiDu Map Location
	private boolean mDisplay_city = false;
	private LocationClient mLocationClient;
	private String mPostCity = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		typeId = getTypeId(getIntent());
		mRootView = getLayoutInflater().inflate(R.layout.photo_activity_post_topic, null);
		setContentView(mRootView);
		initTitleBar();
		iniiCityDisplay(mRootView);
		mHandler = new MyHandler();
		mManager = new PostDataManager(this, mRootView, mHandler);
		initLocation();
		recoverLastEditing(typeId);
	}

	public void setCropParams(CropParams cp) {
		this.mCropParams = cp;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearDraft();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mManager.fluchCache();
	}
	
	   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e(requestCode+"requestCode");
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_TEXT) {
                if (data != null) {
                    int position = data.getIntExtra("position", -1);
                    if (position >= 0) {
                        String text = data.getStringExtra("content");
                        mManager.textRefresh(position, text);
                    }
                }
            }
        }
    }

	private int getTypeId(Intent intent) {
		return intent.getIntExtra(SNSAPI.KEY_FORUMID, -1);
	}

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		clearDraft();
		if (mManager.mSectionList.size() > 0) {
			saveDraftDialog();
		} else {
			super.onBackPressed();
		}
	}

	private void recoverLastEditing(int typeId) {
		if (TopicUtil.checkTopicExist(this, typeId)) {
			// TODO
			TopicUtil topic = TopicUtil.loadTopic(this, typeId);
			mManager.mSectionList = topic.contents;
			mManager.mTitleEtitor.setText(topic.title);
			mManager.refresh(0);
		}
	}

	private void iniiCityDisplay(View parent) {

		final TextView loc_city = (TextView) parent.findViewById(R.id.loc_city);
		final ImageView close_city = (ImageView) parent.findViewById(R.id.disable_loc_city);
		loc_city.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDisplay_city) {
					loc_city.setText(R.string.display_city);
					loc_city.setTextColor(getResources().getColor(R.color.black));
					close_city.setVisibility(View.GONE);
					mDisplay_city = false;
				} else {
					loc_city.setTextColor(getResources().getColor(R.color.black));
					close_city.setVisibility(View.VISIBLE);
					startLocate(loc_city);
				}
			}
		});
		close_city.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDisplay_city = false;
				close_city.setVisibility(View.GONE);
				loc_city.setText(R.string.display_city);
				loc_city.setTextColor(getResources().getColor(R.color.black));
			}
		});
	}

	private void saveDraftDialog() {
		final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
		final View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_save_draft, null);
		final TextView yes = (TextView) view.findViewById(R.id.draft_yes);
		final TextView no = (TextView) view.findViewById(R.id.draft_no);
		dialog.setCancelable(false);
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				TopicUtil.removewTopic(PostActivity.this, typeId);
				PostActivity.this.finish();
			}
		});
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TopicUtil.saveTopic(PostActivity.this, genTopicDetail(), typeId);
				dialog.dismiss();
				PostActivity.this.finish();
			}
		});
		// set up the custom view width.
		view.setMinimumWidth((int) (0.6 * DeviceInfo.scrrenWidth(this)));
		dialog.setContentView(view);
		dialog.show();
	}

	private void clearDraft() {
		for (int i = 0; i < Bimp.tempSelectBitmap.size(); ++i) {
			Bimp.tempSelectBitmap.get(i).isUsed = false;
		}
		Bimp.tempSelectBitmap.clear();
		Bimp.max = 0;
	}

	@Override
	public CropParams getCropParams() {
		// TODO Auto-generated method stub
		return mCropParams;
	}

	@Override
	public void onCropCancel() {
		Toast.makeText(this, "Crop canceled!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCropFailed(String message) {
		Toast.makeText(this, "Crop failed:" + message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onPhotoCropped(Uri uri) {
		// Log.e("onPhotoCropped", uri.getPath());
		int index = mManager.getCurrentIndex();
		final SectionData sd = new SectionData();
		sd.setSectionType(1);
		sd.setmRealValue(uri.getPath());
		Bitmap bm = ImageUtil.compressedBitmap(uri.getPath(), ImageUtil.REQUESR_WIDTH, ImageUtil.REQUEST_HEIGHT);
		try {
			sd.compressedPath = FileUtils.savaBitmap(PostActivity.this, StringUtils.getPicName(uri.getPath()),bm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mManager.insertSection(index + 1, sd);
		mManager.refresh(index +1);
	}

	@Override
	protected void onMultiPhotoSelected() {
		new Thread(new MyRunnable(mHandler)).start();
	}

	private void initTitleBar() {
		mTitleBar = getTitleBar();
		if (mTitleBar != null) {
			mPostAction = mTitleBar.showRightAction(R.string.post, -1);
			mPostAction.setTextColor(getResources().getColor(R.color.gray));
			mPreviewAction = mTitleBar.showPreRightAction(R.string.preview, -1);
			mPreviewAction.setTextColor(getResources().getColor(R.color.gray));
		}
	}

	private String genTopicDetail() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<SectionData> sectionsList = mManager.getSectionsList();
		try {
			map.put("userId", UserManager.getLoginUserId());
			map.put("title", mManager.getTitle());
			map.put("typeId", typeId);
			map.put("postCity", mPostCity);
			map.put("contents", sectionsList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Gson().toJson(map);
	}

	@Override
	protected void onTopicPreviewDone(int resultCode) {
		if (resultCode == PREVIEW_RETURN2EDIT) {
			updatePreviewAction();
		} else if (resultCode == RREVIEW_POST) {
			doUpload(mManager.getSelectedCompressedPics());
		}
	}

	// ************************************************************************/
	private void handleResult(Exception e, JsonObject result) {
		if (e != null) {
			// usually happens due to network issue
			handleError(e.getMessage());
		} else if (result != null && !result.isJsonNull()) {
			JsonElement rescode = result.get(SNSAPI.RESULT_CODE);
			if (SNSAPI.RESULT_CODE_ERROR.equals(rescode.getAsString())) {
				JsonElement resmsg = result.get(SNSAPI.RESULT_MESSAGE);
				handleError(resmsg.getAsString());
			} else {
				handleResult(result);
			}
		}
	}

	public void doUpload(List<String> paths) {
		try {
			final B ionB = Ion.with(this).load(SNSAPI.getPostTopic()).uploadProgress(new ProgressCallback() {
				@Override
				public void onProgress(long current, long total) {
					// TODO Auto-generated method stub

					DecimalFormat df = new DecimalFormat("0.00");
					double d = (double) current / (double) total;
					String strD = df.format(d);
					Double.parseDouble(strD);
					final float count = (float) (Double.parseDouble(strD) * 100);

					Message msg = mHandler.obtainMessage();
					msg.what = UPDATE_UPLOADING_PROGRESS;
					msg.obj = "(" + "" + (int) count + "/" + 100 + ")";
					// LogUtils.e(msg.obj.toString());
					mHandler.sendMessage(msg);

				}
			});
			ionB.setTimeout(1000 * 30);
			ionB.addQuery("loginUserId", UserManager.getLoginUserId()).addQuery("token", UserManager.getToken())
					.addQuery("topicDetail", genTopicDetail());

			for (int i = 0; i < paths.size(); ++i) {
				ionB.setMultipartFile("img" + i, new File(paths.get(i)));
			}

			// ionB.asString().setCallback(new FutureCallback<String>()
			ionB.asJsonObject().setCallback(new FutureCallback<JsonObject>() {

				@Override
				public void onCompleted(Exception ex, JsonObject json) {
					// TODO Auto-generated method stub
					handleResult(ex, json);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	// private void uploadContents(List<String> paths) {
	//
	// String url = SNSAPI.Test_BASE_URL + "/comm/test/fileItem";
	//
	// url = "http://192.168.1.17/comm/fileup";
	// // url = "http://192.168.1.17/comm/test/fileItem";
	// RequestParams params = new RequestParams(SNSAPI.getPostTopic());
	// params.setMultipart(true);
	// // params.setConnectTimeout(10 * 1000);
	// params.addBodyParameter("loginUserId", UserManager.getLoginUserId());
	// params.addBodyParameter("token", UserManager.getToken());
	// params.addBodyParameter("topicDetail", genTopicDetail());
	// for (int i = 0; i < paths.size(); ++i) {
	// String filename = StringUtils.getPicName(paths.get(i));
	// params.addBodyParameter("imgFile" + i,
	// ImageUtil.compressBitmap2InputStream(this, paths.get(i)), "image/*",
	// filename);
	// }
	//
	// Cancelable mCanCelable = x.http().post(params, new
	// Callback.ProgressCallback<JSONObject>() {
	//
	// @Override
	// public void onError(Throwable ex, boolean isOnCallback) {
	// TopicUtil.saveTopic(PostActivity.this, genTopicDetail(), typeId);
	// Toast.makeText(PostActivity.this, R.string.post_failed, 2000).show();
	// }
	//
	// @Override
	// public void onCancelled(CancelledException cex) {
	// Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
	// TopicUtil.saveTopic(PostActivity.this, genTopicDetail(), typeId);
	// // stopProgressDialog();
	// }
	//
	// @Override
	// public void onFinished() {
	// // Log.e("onFinished", "onFinished:");
	// stopProgressDialog();
	// finish();
	// }
	//
	// @Override
	// public void onSuccess(JSONObject arg0) {
	// // TODO Auto-generated method stub
	// // Log.e("onSuccess", "onSuccess:" + arg0.toString());
	// TopicUtil.removewTopic(PostActivity.this, typeId);
	// Toast.makeText(PostActivity.this, R.string.post_scuuess, 2000).show();
	// // LogUtils.e("http_success", arg0.toString());
	// }
	//
	// @Override
	// public void onLoading(long total, long current, boolean arg2) {
	// // TODO Auto-generated method stub
	//
	// Message msg = mHandler.obtainMessage();
	// msg.what = UPDATE_UPLOADING_PROGRESS;
	//
	// DecimalFormat df = new DecimalFormat("0.00");
	// double d = current / total;
	// String strD = df.format(d);
	// Double.parseDouble(strD);
	// final int count = (int) (Double.parseDouble(strD) * 100);
	// msg.obj = "(" + "" + count + "/" + 100 + ")";
	// mHandler.sendMessage(msg);
	// }
	//
	// @Override
	// public void onStarted() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onWaiting() {
	// // TODO Auto-generated method stub
	//
	// }
	// });
	// // mProgress.setMessage(R.string.uploading);
	// mProgress.setCancelTransfer(mCanCelable);
	// }

	class MyRunnable implements Runnable {

		private Handler mHandler;

		public MyRunnable(Handler handler) {
			mHandler = handler;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int index = mManager.getCurrentIndex();
			for (int i = 0; i < Bimp.tempSelectBitmap.size(); ++i) {
				final SectionData sd = new SectionData();
				sd.setSectionType(1);// means pics
				String imgPath = Bimp.tempSelectBitmap.get(i).getImagePath();
				boolean isUsed = Bimp.tempSelectBitmap.get(i).isUsed;
				Bitmap bm = null;

				if (!isUsed) {

					bm = ImageUtil.compressedBitmap(imgPath, ImageUtil.REQUESR_WIDTH, ImageUtil.REQUEST_HEIGHT);
					try {
						// if (bm == null) {
						// bm =
						// ImageUtil.revitionImageSize(Bimp.tempSelectBitmap.get(i).thumbnailPath);
						// }
						sd.compressedPath = FileUtils.savaBitmap(PostActivity.this, StringUtils.getPicName(imgPath),
								bm);
						if (null == sd.compressedPath) {
							sd.compressedPath = Bimp.tempSelectBitmap.get(i).thumbnailPath;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Bimp.tempSelectBitmap.get(i).isUsed = true;
					sd.setmRealValue(imgPath);
//					mManager.insertSection(index + 1, sd);
					//very importance, call is in UI thread,not in current thread.
					++index;
					Message msg = mHandler.obtainMessage();
					msg.arg1 = index;
					msg.obj = sd;
					msg.what = UPDATE_LIST;
					mHandler.sendMessage(msg);
				}
			}
		}
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			super.handleMessage(msg);
			switch (msg.what) {

			case DISABLE_PREVIEW_POST: {
				disableActions();
				break;
			}
			case ENABLE_PREVIEW_POST: {
				enableActions();
				break;
			}
			case GET_CropParams: {
				mCropParams = (CropParams) msg.obj;
				break;
			}
			case UPDATE_LIST: {
				mManager.insertSection(msg.arg1, (SectionData)msg.obj);
				mManager.refresh(msg.arg1);
				break;
			}
			case UPDATE_UPLOADING_PROGRESS: {
				if (mProgress != null) {
					mProgress.setMessage("正在上传" + msg.obj.toString());
				}
				break;
			}

			case CANCEL_UPLOADING: {
				cancelAll();
				break;
			}
			}
		}
	}

	private void enableActions() {
		mTitleBar = getTitleBar();
		if (mTitleBar != null) {
			mPostAction.setTextColor(getResources().getColor(R.color.white));
			mPostAction.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new AsyncLoadingProgress().execute("");
				}
			});
			mPreviewAction.setTextColor(getResources().getColor(R.color.white));
			mPreviewAction.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mPreviewAction.setVisibility(View.INVISIBLE);
					Intent intent = new Intent();
					intent.putExtra("topic", genTopicDetail());
					ActivityLauncher.startTopicPreviewActivity(PostActivity.this, intent,
							PREVIEW_ACTIVITY_REQUEST_CODE);
				}

			});
		}
	}

	private void disableActions() {
		mTitleBar = getTitleBar();
		if (mTitleBar != null) {
			mPostAction.setTextColor(getResources().getColor(R.color.gray));
			mPostAction.setClickable(false);
			mPreviewAction.setTextColor(getResources().getColor(R.color.gray));
			mPreviewAction.setClickable(false);
		}
	}

	private void updatePreviewAction() {
		mPreviewAction.setVisibility(View.VISIBLE);
	}

	private void startProgressDialog() {
		if (mProgress == null) {
			mProgress = LoadingProgressDialog.createDialog(PostActivity.this);
			mProgress.setMessage(R.string.uploading);
		}
		mProgress.show();
	}

	private void stopProgressDialog() {
		if (mProgress != null) {
			mProgress.dismiss();
			mProgress = null;
		}
	}

	class AsyncLoadingProgress extends AsyncTask<String, Integer, Object> {

		@Override
		protected Object doInBackground(String... params) {
			// 后续代码
			// uploadContents(mManager.getSelectedPics());
			doUpload(mManager.getSelectedCompressedPics());
			return null;
		}

		protected void onPostExecute(String result) {
			// stopProgressDialog();
			// 后续代码
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startProgressDialog();
		}
	}

	private void initLocation() {
		mLocationClient = ((GlobalApplication) getApplication()).mLocationClient;
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("gcj02");// 可选，默认gcj02，设置返回的定位结果坐标系，
		// int span = 3;
		// option.setScanSpan(span);//
		// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		mLocationClient.setLocOption(option);

	}

	private void startLocate(final TextView tvCity) {
		MyLocationListener mMyLocationListener = new MyLocationListener(tvCity);
		mLocationClient.registerLocationListener(mMyLocationListener);
		mLocationClient.start();// 定位SDK
		mLocationClient.requestLocation();
	}

	/**
	 * 实现实时位置回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		private TextView loc_city;

		public MyLocationListener(final TextView loc_city) {
			// TODO Auto-generated constructor stub
			this.loc_city = loc_city;
		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			String city = location.getCity();
			if (city != null) {
				loc_city.setText(city);
				loc_city.setTextColor(getResources().getColor(R.color.black));
				mLocationClient.unRegisterLocationListener(this);
				mLocationClient.stop();
				mDisplay_city = true;
				mPostCity = city;
			} else {
				loc_city.setText(R.string.retry_loc);
				loc_city.setTextColor(getResources().getColor(R.color.red));
				mDisplay_city = false;
			}

		}

	}

	@Override
	protected void handleError(String errmsg) {
		handleError(errmsg, false);
		stopProgressDialog();
//		TopicUtil.saveTopic(PostActivity.this, genTopicDetail(), typeId);
//		Toast.makeText(PostActivity.this, R.string.post_failed, 2000).show();
//		finish();
	}

	@Override
	protected void handleResult(JsonObject result) {
		stopProgressDialog();
		TopicUtil.removewTopic(PostActivity.this, typeId);
		Toast.makeText(PostActivity.this, R.string.post_scuuess, 2000).show();
		finish();
	}

}
