package com.xiakee.xkxsns.bean;

/**
 * Created by William on 2015/11/5.
 */
public class MyInfo extends BaseBean {
    public MyInfoData userInfo;

    @Override
    public String toString() {
        return "UserInfo{" +
                "userInfo=" + userInfo +
                '}';
    }

    public static class MyInfoData {

        public String bbsCount;
        public String focusCount;
        public String fansCount;
        public String photo;
        public String userId;
        public String userName;
        public String signStatus;

        @Override
        public String toString() {
            return "UserInfo{" +
                    "bbsCount='" + bbsCount + '\'' +
                    ", focusCount='" + focusCount + '\'' +
                    ", fansCount='" + fansCount + '\'' +
                    ", photo='" + photo + '\'' +
                    ", userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", signStatus='" + signStatus + '\'' +
                    '}';
        }
    }

}
