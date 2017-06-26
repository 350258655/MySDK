package action.hdsdk.com.sdk.listener;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import action.hdsdk.com.sdk.dialog.OrderDialog;

/**
 * Created by shake on 2017/6/26.
 */
public abstract class WxPayListener implements Serializable {

    public abstract void onSuccess();

    public abstract void onFail();





}
