package action.hdsdk.com.sdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shake on 2017/6/22.
 */
public class HDApplication extends Application {

    private static Context sContext;

    public static String access_token; // 全局的access_token

    private List<Activity> mList = new LinkedList<Activity>();

    private static HDApplication sApplication; // Application实例


    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }


    /**
     * 获取HDApplication实例
     *
     * @return
     */
    public static HDApplication getInstance() {
        if (sApplication == null) {
            synchronized (HDApplication.class) {
                if (sApplication == null) {
                    sApplication = new HDApplication();
                }
            }
        }
        return sApplication;
    }


    /**
     * 往集合中添加Activity
     * @param activity
     */
    public void addActivity(Activity activity){
        mList.add(activity);
    }

    /**
     * 结束Activity
     */
    public void finishActivity(){
        for (Activity activity : mList) {
            if(activity != null){
                activity.finish();
            }
        }

        // 清除集合中的内容
        mList.clear();
    }



    /**
     * 获取全局的context
     *
     * @return
     */
    public static Context getContext() {
        return sContext;
    }
}
