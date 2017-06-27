package action.hdsdk.com.sdk.widget;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class FloatViewService extends Service {

    private FloatView mFloatView;

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatView = new FloatView(this);
    }

    /**
     * 显示悬浮框
     */
    public void showFloatView(){
        if(mFloatView != null){
            mFloatView.show();
        }
    }

    /**
     * 隐藏悬浮框
     */
    public void hideFloatView(){
        if(mFloatView != null){
            mFloatView.hide();
        }
    }

    /**
     * 销毁悬浮框
     */
    public void destroyFloatView(){
        if(mFloatView != null){
            mFloatView.destroy();
        }
        mFloatView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyFloatView();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return new FloatViewServiceBinder();
    }


    public class FloatViewServiceBinder extends Binder {
        public FloatViewService getService(){
            return FloatViewService.this;
        }
    }
}
