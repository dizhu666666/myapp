package com.xiakee.xkxsns.bean;

/**
 * Created by William on 2015/11/26.
 */
public class LabelInfo extends BaseBean {
    public LabelInfoData labelInfo;


    @Override
    public String toString() {
        return "LabelInfo{" +
                "labelInfo=" + labelInfo +
                '}';
    }

    public static class LabelInfoData{
        public String labelId;
        public String title;
        public String logo;
        public String type;
        public String focusStatus;
        public String topicCount;
        public String labelDesc;

        @Override
        public String toString() {
            return "LabelInfoData{" +
                    "labelId='" + labelId + '\'' +
                    ", title='" + title + '\'' +
                    ", logo='" + logo + '\'' +
                    ", type='" + type + '\'' +
                    ", focusStatus='" + focusStatus + '\'' +
                    ", topicCount='" + topicCount + '\'' +
                    ", labelDesc='" + labelDesc + '\'' +
                    '}';
        }
    }
}
