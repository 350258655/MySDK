package action.hdsdk.com.sdk.db;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.HDApplication;

/**
 * Created by shake on 2017/6/27.
 * 存储已经绑定手机的用户
 */
public class BindPhoneUser {

    private static Gson mGson;

    /**
     * 添加绑定手机用户
     */
    public static void addBindUser(String userName,String phone) {

        Map<String, String> userList = getAllBindUser();
        userList.put(userName,phone);

        // 转换成字符串之后，存在sp中
        String userListMsg = userList.toString();
        PreferencesUtils.putString(HDApplication.getContext(), Const.BIND_PHONE_USER, userListMsg);

    }


    /**
     * 获取全部绑定手机的用户
     *
     * @return
     */
    public static Map<String, String> getAllBindUser() {
        String msg = PreferencesUtils.getString(HDApplication.getContext(), Const.BIND_PHONE_USER);
        Map<String, String> map = new HashMap<>();
        // 一开始应该是null
        if (msg == null) {
            return map;
        }

        if (mGson == null) {
            mGson = new Gson();
        }

        map = mGson.fromJson(msg, map.getClass());
        return map;
    }


}
