package com.android.util;

import com.xiakee.xkxsns.ui.activity.CommonForumActivity;
import com.xiakee.xkxsns.ui.activity.LoginActivity;
import com.xiakee.xkxsns.ui.activity.PostActivity;
import com.xiakee.xkxsns.ui.activity.ReplyMeActivity;
import com.xiakee.xkxsns.ui.activity.TopicPreviewActivity;
import com.xiakee.xkxsns.ui.activity.UserSpaceActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.os.Bundle;

public class ActivityLauncher {

	public static void startCommonForumActivity(Context context, Intent intent) {
		// Intent intent = new Intent(context, CommonForumActivity.class);
		intent.setClass(context, CommonForumActivity.class);
		context.startActivity(intent);
	}

	public static void startPostActivity(Context context, Intent intent) {
		// Intent intent = new Intent(context, CommonForumActivity.class);
		intent.setClass(context, PostActivity.class);
		context.startActivity(intent);
	}

	public static void startLoginActivity(Context context, Intent intent) {
		// Intent intent = new Intent(context, CommonForumActivity.class);
		intent.setClass(context, LoginActivity.class);
		context.startActivity(intent);
	}

	public static void startPhotosSeclectActivity(Activity ac, Intent intent, int requestCode) {
		// intent.setClass(context, PostActivity.class);
		ac.startActivityForResult(intent, requestCode);
	}

	public static void startTopicPreviewActivity(Activity ac, Intent intent, int requestCode) {
		intent.setClass(ac, TopicPreviewActivity.class);
		ac.startActivityForResult(intent, requestCode);
	}

	public static void startUserSpaceActivity(Context c, Intent intent) {
		intent.setClass(c, UserSpaceActivity.class);
		c.startActivity(intent);
	}

	public static void startReplyMeActivity(Context context, Intent intent) {
		// TODO Auto-generated method stub
		intent.setClass(context, ReplyMeActivity.class);
		context.startActivity(intent);
	}

	/*
	 * public static void startCoachActivity(Context context, Intent intent) {
	 * // Intent intent = new Intent(context, CoachActivity.class);
	 * intent.setClass(context, CoachActivity.class);
	 * context.startActivity(intent); }
	 * 
	 * public static void startCoachDetailedActivity(Context context, Bundle
	 * bundle) { Intent intent = new Intent(context,
	 * CoachDetailsActivity.class); intent.putExtras(bundle);
	 * context.startActivity(intent); }
	 * 
	 * public static void startCommentCoachActivity(Context context, Intent
	 * intent) { // Intent intent = new Intent(context,
	 * CommentCoachActivity.class); intent.setClass(context,
	 * CommentCoachActivity.class); context.startActivity(intent); }
	 * 
	 * public static void startCommentsListActivity(Context context, Bundle
	 * bundle) { Intent intent = new Intent(context,
	 * CommentsListActivity.class); intent.putExtras(bundle);
	 * context.startActivity(intent); }
	 * 
	 * public static void startOrderDetailActivity(Context context, Bundle
	 * bundle) { Intent intent = new Intent(context, OrderDetailActivity.class);
	 * intent.putExtras(bundle); context.startActivity(intent); }
	 * 
	 * public static void startCourseIntroduceActivity(Context context, Bundle
	 * bundle) { Intent intent = new Intent(context,
	 * CourseBuyDetailActivity.class); intent.putExtras(bundle);
	 * context.startActivity(intent); }
	 * 
	 * public static void startCourseBuyDetailActivity(Context context, Intent
	 * intent) { intent.setClass(context, CourseBuyDetailActivity.class); //
	 * Intent intent = new Intent(context, CourseIntroduceActivity.class); //
	 * intent.putExtras(bundle); context.startActivity(intent);
	 * 
	 * }
	 * 
	 * public static void startFragment(Fragment fm, Context context, Bundle
	 * data, int containerId) { // FragmentCoachInfo fm = new
	 * FragmentCoachInfo(); final FragmentTransaction transaction =
	 * ((FragmentActivity) context)
	 * .getSupportFragmentManager().beginTransaction(); // Bundle bundle = new
	 * Bundle(); // bundle.putParcelable("coachInfo", mDetailInfo);
	 * fm.setArguments(data); // View view =
	 * findViewById(R.id.fragment_coachinfo); //
	 * mFragmentView.setVisibility(View.VISIBLE); transaction.add(containerId,
	 * fm); // transaction.add(containerId, fm);
	 * transaction.addToBackStack(null); transaction.commit(); }
	 * 
	 * public static void startGymDetailActivity(Context context, Bundle bundle)
	 * { Intent intent = new Intent(context, GymDetailActivity.class);
	 * intent.putExtras(bundle); context.startActivity(intent); }
	 * 
	 * 
	 * public static void startGymIntroduceActivity(Context context, Bundle
	 * bundle) { Intent intent = new Intent(context,
	 * GymIntroducetionActivity.class); intent.putExtras(bundle);
	 * context.startActivity(intent); }
	 * 
	 * public static void startGymShowActivity(Context context) { Intent intent
	 * = new Intent(context, GymDetailActivity.class);
	 * context.startActivity(intent); }
	 * 
	 * public static void startLocationActivity(Context context, Intent intent)
	 * { intent.setClass(context, LocationActivity.class);
	 * context.startActivity(intent); }
	 * 
	 * public static void startLoginActivity(Context context) { Intent intent =
	 * new Intent(context, LoginActivity.class); context.startActivity(intent);
	 * ((Activity) context).overridePendingTransition(R.anim.anim_b2t,
	 * R.anim.anim_t2b); }
	 * 
	 * public static void startLoginActivity(FragmentMy fm, Context context,
	 * Intent urlintent) {
	 * 
	 * urlintent.setClass(context, LoginActivity.class);
	 * fm.startActivityForResult(urlintent, Activity.RESULT_FIRST_USER); }
	 * 
	 * public static void startMainActivity(Context context) { Intent intent =
	 * new Intent(context, MainActivity.class); context.startActivity(intent); }
	 * 
	 * public static void startMyBaseInfoActivity(FragmentMy fm, Context
	 * context, Intent intent) { intent.setClass(context,
	 * MyBaseInfoActivity.class); fm.startActivityForResult(intent,
	 * Activity.RESULT_FIRST_USER); }
	 * 
	 * // public static void startSettingActivity(Context context, Intent //
	 * urlintent) { // urlintent.setClass(context, SettingActivity.class); //
	 * ((MainActivity)context).startActivityForResult(urlintent, //
	 * BaseActivity.REQUESTCODE); // }
	 * 
	 * public static void startMyCollectionsActivity(Context context, Intent
	 * intent) { intent.setClass(context, MyCollectionsActivity.class);
	 * context.startActivity(intent); }
	 * 
	 * 
	 * public static void startMyCouponsActivity(Context context) { Intent
	 * intent = new Intent(context, MyCouponsActivity.class);
	 * context.startActivity(intent);
	 * 
	 * }
	 * 
	 * 
	 * public static void startOrderActivity(Context context) { Intent intent =
	 * new Intent(context, OrderRecordActivity.class);
	 * context.startActivity(intent); }
	 * 
	 * public static void startSettingActivity(FragmentMy fm, Context context,
	 * Intent urlintent) {
	 * 
	 * urlintent.setClass(context, SettingActivity.class);
	 * fm.startActivityForResult(urlintent, Activity.RESULT_FIRST_USER); }
	 * 
	 * public static void startWebviewActivity(Context context, Intent
	 * urlintent) { urlintent.setClass(context, WebviewActivity.class);
	 * context.startActivity(urlintent); }
	 * 
	 * public static void startWebviewActivity(Context context, String url) {
	 * Intent intent = new Intent(); intent.putExtra("url", url);
	 * intent.setClass(context, WebviewActivity.class);
	 * context.startActivity(intent); }
	 */
}
