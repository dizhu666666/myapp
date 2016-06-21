package com.xiakee.xkxsns.bean.TopicDetailsBean;

/**
 * Created by William on 2015/11/7.
 */
public class TopicDetailsGoodUser {
    /***
     * goodUsers":[{"userId":1899,"photo":"/commpics/bbs/2015/10/21a71ba9-a701-4134-80d1-b101844b9a43.png"}]}}
     */

    public String userId;
    public String photo;

    @Override
    public String toString() {
        return "TopicDetailsGoodUser{" +
                "userId='" + userId + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
