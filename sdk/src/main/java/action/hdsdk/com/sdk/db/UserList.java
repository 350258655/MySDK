package action.hdsdk.com.sdk.db;

import android.content.Context;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.dialog.ProgressDialogUtils;
import action.hdsdk.com.sdk.utils.Utils;

/**
 * Created by shake on 2017/6/21.
 * 用户列表
 */
public class UserList {

    private static Gson mGson;

    /**
     * 获取最新的那个用户
     *
     * @param context
     * @return
     */
    public static String[] getFirstUser(Context context) {
        // 获取全部用户
        Map<String, String> allUser = getAllUser(context);

        // 一开始并没有缓存数据的
        if (allUser.isEmpty()) {
            return null;
        }

        // 获取最新的用户的那个key
        String lastUserKey = allUser.get(Const.LASR_USER);
        String lastUserPwd = allUser.get(lastUserKey);
        lastUserPwd = lastUserPwd.split("&")[0];
//        // 根据key来获取最新的user、因为密码都是6位数字，所以这里可能要转换一下
//        Object lastUserPsw = allUser.get(lastUserKey);
//        Double doubleUser = (Double) lastUserPsw;
//        int intUser = (int) (doubleUser*10/10);

        String password = String.valueOf(lastUserPwd);

        // 创建一个字符串数据返回
        String[] lastUser = new String[]{lastUserKey, password};

        Utils.log(UserList.class, "最新的那个用户：" + lastUserKey);
        return lastUser;
    }


    /**
     * 获取全部用户。加上最后的标识
     *
     * @param context
     * @return
     */
    private static Map<String, String> getAllUser(Context context) {

        String msg = PreferencesUtils.getString(context, Const.USER_LIST);
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

    /**
     * 获取要显示在userList上的数据
     *
     * @param context
     * @return
     */
    public static Map<String, String> getUserList(Context context) {
        Map<String, String> userList = getAllUser(context);
        userList.remove(userList.get(Const.LASR_USER));
        userList.remove(Const.LASR_USER);
        return userList;
    }


    /**
     * 添加用户
     *
     * @param user
     */
    public static void addUser(String[] user, Context context) {
        Map<String, String> userList = getAllUser(context);
        // 先把最新用户标识存下去
        userList.put(Const.LASR_USER, user[0]);
        // 再把这个最新的用户存下去。存的时候拼接一些字符串，免得出错
        userList.put(user[0], user[1] + "&hdGame");

        // 转换成字符串之后，存在sp中
        String userListMsg = userList.toString();
        PreferencesUtils.putString(context, Const.USER_LIST, userListMsg);
    }


    /**
     * 删除用户
     *
     * @param userName
     * @param context
     */
    public static void removeUser(String userName, Context context) {
        Map<String, String> userList = getAllUser(context);
        if (userList.containsKey(userName)) {
            userList.remove(userName);

            // 转换成字符串之后，存在sp中
            String userListMsg = userList.toString();
            PreferencesUtils.putString(context, Const.USER_LIST, userListMsg);
        }

    }


}
