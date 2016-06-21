package com.xiakee.xkxsns.bean;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by William on 2015/11/7.
 */
public class Comment extends BaseBean {
    public List<CommentData> topicComments;

    /***
     * {"resmsg":"",
     * "topicComments":
     * [{"commentTime":"2015-10-31 10:54","img":"","commentId":33,"commentType":1,
     * "title":"2206206hello","userId":1898,"userName":"地主","photo":"","parentUserId":1899,"parentUserName":"用户名1899"}],
     * "rescode":"00000"}
     */


    /***
     * commentsList":[{"commentTime":"2015-10-30 14:30","img":"",
     * "commentId":20,"commentType":1,"title":"206206hello","userId":1898,"userName":"本人单身，从不吭声。","photo":"",
     * "commentsList":[],"parentUserId":1899,"parentUserName":"说你可怜"},{"commentTime":"2015-10-30 14:31","img":"","
     * commentId":21,"commentType":1,"title":"206206hello","userId":1898,"userName":"本人单身，从不吭声。","photo":"",
     * "commentsList":[],"parentUserId":1899,"parentUserName":"说你可怜"}]
     */
    public static class CommentData  implements Parcelable{
        public String commentTime;
        public String img;
        public String commentId;
        public String commentType;
        public String title;
        public String userId;
        public String userName;
        public String photo;
        public String parentUserId;
        public String parentUserName;
        public String commentsGoodStatus;
        public int floor;//楼层
        public int goodCount;//点赞数量
        public int lv;//等级
        public String sex;

        public List<CommentData2> commentsList;


        protected CommentData(Parcel in) {
            commentTime = in.readString();
            img = in.readString();
            commentId = in.readString();
            commentType = in.readString();
            title = in.readString();
            userId = in.readString();
            userName = in.readString();
            photo = in.readString();
            parentUserId = in.readString();
            parentUserName = in.readString();
            commentsGoodStatus = in.readString();
            floor = in.readInt();
            goodCount = in.readInt();
            lv = in.readInt();
            sex = in.readString();
            commentsList = in.createTypedArrayList(CommentData2.CREATOR);
        }

        public static final Creator<CommentData> CREATOR = new Creator<CommentData>() {
            @Override
            public CommentData createFromParcel(Parcel in) {
                return new CommentData(in);
            }

            @Override
            public CommentData[] newArray(int size) {
                return new CommentData[size];
            }
        };

        @Override
        public String toString() {
            return "CommentData{" +
                    "commentTime='" + commentTime + '\'' +
                    ", img='" + img + '\'' +
                    ", commentId='" + commentId + '\'' +
                    ", commentType='" + commentType + '\'' +
                    ", title='" + title + '\'' +
                    ", userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", photo='" + photo + '\'' +
                    ", parentUserId='" + parentUserId + '\'' +
                    ", parentUserName='" + parentUserName + '\'' +
                    ", commentsGoodStatus='" + commentsGoodStatus + '\'' +
                    ", floor=" + floor +
                    ", goodCount=" + goodCount +
                    ", lv=" + lv +
                    ", sex='" + sex + '\'' +
                    ", commentsList=" + commentsList +
                    '}';
        }



        public boolean isPraise() {
            return "1".equals(commentsGoodStatus);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(commentTime);
            dest.writeString(img);
            dest.writeString(commentId);
            dest.writeString(commentType);
            dest.writeString(title);
            dest.writeString(userId);
            dest.writeString(userName);
            dest.writeString(photo);
            dest.writeString(parentUserId);
            dest.writeString(parentUserName);
            dest.writeString(commentsGoodStatus);
            dest.writeInt(floor);
            dest.writeInt(goodCount);
            dest.writeInt(lv);
            dest.writeString(sex);
            dest.writeTypedList(commentsList);
        }
    }

    public static class CommentData2 implements Parcelable {
        public String commentTime;
        public String img;
        public String commentId;
        public int commentType;
        public String title;
        public String userId;
        public String userName;
        public String photo;
        public String parentUserId;
        public String parentUserName;


        protected CommentData2(Parcel in) {
            commentTime = in.readString();
            img = in.readString();
            commentId = in.readString();
            commentType = in.readInt();
            title = in.readString();
            userId = in.readString();
            userName = in.readString();
            photo = in.readString();
            parentUserId = in.readString();
            parentUserName = in.readString();
        }

        public static final Creator<CommentData2> CREATOR = new Creator<CommentData2>() {
            @Override
            public CommentData2 createFromParcel(Parcel in) {
                return new CommentData2(in);
            }

            @Override
            public CommentData2[] newArray(int size) {
                return new CommentData2[size];
            }
        };

        @Override
        public String toString() {
            return "CommentData{" +
                    "commentTime='" + commentTime + '\'' +
                    ", img='" + img + '\'' +
                    ", commentId='" + commentId + '\'' +
                    ", commentType='" + commentType + '\'' +
                    ", title='" + title + '\'' +
                    ", userId='" + userId + '\'' +
                    ", userName='" + userName + '\'' +
                    ", photo='" + photo + '\'' +
                    ", parentUserId='" + parentUserId + '\'' +
                    ", parentUserName='" + parentUserName + '\'' +
                    '}';
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(commentTime);
            dest.writeString(img);
            dest.writeString(commentId);
            dest.writeInt(commentType);
            dest.writeString(title);
            dest.writeString(userId);
            dest.writeString(userName);
            dest.writeString(photo);
            dest.writeString(parentUserId);
            dest.writeString(parentUserName);
        }
    }

    @Override
    public String toString() {
        return "Comment{" +
                "topicComments=" + topicComments +
                '}';
    }
}
