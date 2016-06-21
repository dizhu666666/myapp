package com.xiakee.xkxsns.bean;

/**
 * Created by William on 2015/11/19.
 */
public class FindPassword extends BaseBean {
    /***
     * {"resmsg":"","lostToken":"7b93de84f0632a198aadc07b718d06a5","rescode":"00000","memberId":1572}
     */

    public String lostToken;
    public String memberId;

    @Override
    public String toString() {
        return "FindPassword{" +
                "lostToken='" + lostToken + '\'' +
                ", memberId='" + memberId + '\'' +
                '}';
    }
}
