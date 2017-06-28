package action.hdsdk.com.sdk.listener;

/**
 * Created by shake on 2017/6/28.
 *
 */
public interface ExitListener {

    /**
     * 退出成功,退出逻辑由CP自己去实现
     */
    void onExitSuccess(String msg);


    /**
     * 取消退出
     */
    void onExitCancle(String msg);

}
