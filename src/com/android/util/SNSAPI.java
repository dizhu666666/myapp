package com.android.util;

public class SNSAPI {
	// request

	public static final int sizeRequest = 4;
	public static final int PULL_UP = 0;
	public static final int PULL_DOWN = 1;
	public static final String USE_DEFAULT = "tag_xiakee";

	public static final int TEST_USERID = 1898;
	public static final String Test_BASE_URL = "http://119.254.211.89";
	// production

	public static final String USERID = "userId";
	public static final String URL_MARKET = "http://www.xiakee.com";

	// response
	public static final String RESULT_CODE = "rescode";
	public static final String RESULT_CODE_ERROR = "00001";
	public static final String RESULT_CODE_OK = "00000";
	public static final String RESULT_GYM_CITY = "city";
	public static final String RESULT_MESSAGE = "resmsg";
	public static final String RESULT_USER_AGE = "age";
	public static final String RESULT_USER_AVATAR = "headPhoto";
	public static final String RESULT_USER_GENDER = "sex";
	public static final String RESULT_USER_ID = "id";
	public static final String RESULT_USER_LOCATION = "area";

	//
	public static final String TOPICS_LIST = "/comm/index/topicList";
	public static final String BANNERLIST = "/comm/index/banners";
	public static final String PRAISE = "/comm/topic/good";
	public static final String FORUMTOPICLIST = "/comm/type/topicList";
	public static final String FORUM_TOP_TOPICLIST = "/comm/type/topicTopList";
	// post topic
	public static final String POST_TOPIC = "/comm/topic/postTopic";
	public static final String QUERY_LABEL = "/comm/label/labelList";
	public static final String FOLLOW_USER = "/comm/person/focus";
	public static final String USER_INFO = "/comm/person/info";

	public static final int HintSize = 12;

	// Forum ID
	public static int SHOW_EQUIPMENT = 1;
	public static int SHOW_OUTSIDE = 2;
	public static int SHARE_KNOWLEDGE = 3;
	public static int SECONDARY_MARKET = 4;

	// Forum name
	public static String KEY_FORUMID = "forum_id";
	public static String KEY_FORUMNAME = "forum_name";
	public static String KEY_FORUMNAME_RESID = "forum_name_resID";

	public static String USER_TOPICS = "/comm/person/myTopic";
	public static String USER_REPLIES = "";
	public static String USER_PRAISED = "";

	// operation:0-->pullup
	// operation:0 -->pulldown
	// 首页热帖列表
	public static String getTopicsListUrl(int marked_topicId, int operation, int userId) {
		return Test_BASE_URL + TOPICS_LIST + "?marked_topicId=" + marked_topicId + "&operation=" + operation
				+ "&loginUserId=" + userId;
	}

	// 首页Banner List
	public static String getBannerListUrl() {
		return Test_BASE_URL + BANNERLIST;
	}

	// 点赞
	public static String getPraiseActionUrl(int topicId, int userId) {
		return Test_BASE_URL + PRAISE + "?topicId=" + topicId + "&loginUserId=" + userId;
	}

	// 某版块帖子列表
	public static String getForumTopicListUrl(int typeId, int topicId, int oper, int userId) {
		return Test_BASE_URL + FORUMTOPICLIST + "?typeId=" + typeId + "&marked_topicId=" + topicId + "&operation="
				+ oper + "&loginUserId=" + userId;
	}

	// 版块置顶帖子列表
	public static String getForumTopTopicListUrl(int typeId, int userId) {
		return Test_BASE_URL + FORUM_TOP_TOPICLIST + "?typeId=" + typeId + "&loginUserId=" + userId;
	}

	// 发帖
	public static String getPostTopic() {
		return Test_BASE_URL + POST_TOPIC;
	}
	
	// 发帖
		public static String geTtestPostTopic() {
			return "http://192.168.1.17" + POST_TOPIC;
		}

	// 获取标签数组
	public static String getLabelQuery(String first, int typeId) {
		return Test_BASE_URL + QUERY_LABEL + "?first=" + first + "&typeId=" + typeId;
	}

	public static String getUserTopics(int loginUserId, int page) {
		return Test_BASE_URL + USER_TOPICS + "?loginUserId=" + loginUserId + "&page=" + page;
	}

	public static String getFollowSomeOneUrl(int userId, int beFollowedUserId) {
		return Test_BASE_URL + FOLLOW_USER + "?loginUserId=" + userId + "&focusUserId=" + beFollowedUserId;
	}

	public static String getUserSpaceInfoUrl(int userId, int topicUserId) {
		return Test_BASE_URL + USER_INFO + "?loginUserId=" + userId + "&topicUserId=" + topicUserId;
	}

	//

}
