package com.xiakee.xkxsns.bean;

import java.util.List;

/**
 * Created by William on 2015/11/3.
 */
public class Focus extends BaseBean {
    public List<FocusData> userList;

    /***
     * {"resmsg":"","rescode":"00000",
     * "userList":[{"photo":"/commpics/bbs/2015/10/21a71ba9-a701-4134-80d1-b101844b9a43.png",
     * "sign":"","userId":1899,"userName":"用户名1899"}]}
     */

    public static class FocusData {
        public String photo;//头像URL
        public String sign;//个性签名
        public String userId;
        public String userName;//昵称
        public String focusType;//1双向关注0单向

        public boolean isMutualFocus() {
            return "1".equals(focusType);
        }

        @Override
        public String toString() {
            return "FocusData{" +
                    "photo='" + photo + '\'' +
                    ", sign='" + sign + '\'' +
                    ", userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", focusType='" + focusType + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Focus{" +
                "userList=" + userList +
                '}';
    }
}
