package com.xiakee.xkxsns.bean.TopicDetailsBean;

/**
 * Created by William on 2015/11/7.
 */
public class TopicDetailsImgLabel {
    /***
     * {"labelId":2,"title":"跑步"},{"labelId":1, "type":1,"title":"装备控"}
     */

    public String labelId;
    public String type;
    public String title;

    @Override
    public String toString() {
        return "TopicDetailsImgLabel{" +
                "labelId='" + labelId + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
