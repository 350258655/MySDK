package action.hdsdk.com.sdk.dialog;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import action.hdsdk.com.sdk.Const;
import action.hdsdk.com.sdk.ForgetPsdActivity;
import action.hdsdk.com.sdk.HDApplication;
import action.hdsdk.com.sdk.R;

import action.hdsdk.com.sdk.db.BindPhoneUser;
import action.hdsdk.com.sdk.db.PreferencesUtils;
import action.hdsdk.com.sdk.db.UserList;
import action.hdsdk.com.sdk.http.API;
import action.hdsdk.com.sdk.http.BaseHttpCallback;
import action.hdsdk.com.sdk.http.HttpCallback;
import action.hdsdk.com.sdk.http.OkHttpHelper;
import action.hdsdk.com.sdk.listener.LoginListener;

import action.hdsdk.com.sdk.utils.ToastUtils;
import action.hdsdk.com.sdk.utils.Utils;
import action.hdsdk.com.sdk.widget.FloatViewService;
import action.hdsdk.com.sdk.widget.SpinnerAdapter;
import action.hdsdk.com.sdk.widget.SpinnerView;

/**
 * Created by shake on 2017/6/20.
 * 登录框
 */
public class LoginDialog extends BaseDialog implements View.OnClickListener {

    private Button mBtn_login;
    private Button mBtn_reg;
    private SpinnerView mSv_username;
    private EditText mEt_username;
    private EditText mEt_password;
    private TextView mTv_forgetPwd;
    private OkHttpHelper mOkHttpHelper;
    private Context mContext;
    private String psw;
    private LoginListener mLoginListener;
    private List<String> datas; // 用户名列表
    private Map<String, String> userList; // 用户名 + 密码 的列表
    private FloatViewService mFloatViewService;

    public LoginDialog(Context context, LoginListener loginListener, FloatViewService floatViewService) {
        super(context, Const.LOGIN_DIALOG);
        // 先加载layout再调用 setContentView(view); 这样父类控制对话框的大小才会起作用
        View view = LayoutInflater.from(context).inflate(R.layout.hd_dialog_login, null);
        setContentView(view);

        // 初始化okHttpHelper
        mOkHttpHelper = OkHttpHelper.getInstance();
        mContext = context;
        mLoginListener = loginListener;
        mFloatViewService = floatViewService;

        // 初始化View
        initViews();

        // 初始化数据
        initDatas(context);

        // 初始化事件
        initEvents();


    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        // 注册监听时间
        mBtn_login.setOnClickListener(this);
        mBtn_reg.setOnClickListener(this);
        mTv_forgetPwd.setOnClickListener(this);

        //设置点击的监听事件，监听后的操作交给用户去使用
        mSv_username.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 获取点击的内容，并且填充到编辑框中
                String data = datas.get(position);
                // 设置编辑框内容
                mSv_username.setText(data);
                // 根据UserName获取密码
                // 根据key来获取最新的user
                String lastUserPwd = userList.get(data);
                lastUserPwd = lastUserPwd.split("&")[0];

                mEt_password.setText(String.valueOf(lastUserPwd));
                // 显示星号
                mEt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                //设置编辑框的光标起点
                mSv_username.setSelection(data.length());

                //隐藏PopupWindow
                mSv_username.dismiss();
            }
        });


        // 忘记密码
        mTv_forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到忘记密码界面，需要携带当前的账号名过去。假如不为空才传过去，因为玩家有可能是记住了以前的帐号，但是SDK是重新安装的，所以界面是不会显示以前的账号名的
                String user = mEt_username.getText().toString().trim();
                Intent intent = new Intent(mContext, ForgetPsdActivity.class);
                if (user != null && !user.equals("")) {
                    intent.putExtra(Const.CURRENT_USER, user);
                }
                mContext.startActivity(intent);

                // 关闭当前Dialog
                dismiss();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initDatas(Context context) {
        // 获取最新用户
        String[] user = UserList.getFirstUser(context);
        if (user != null) {
            Utils.log(LoginDialog.class, "最新用户是：" + user[0]);
            mEt_username.setText(user[0]);
            mEt_password.setText(user[1]);
            // 显示星号
            mEt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        // 设置UserList
        userList = UserList.getUserList(context);
        datas = new ArrayList<>();
        for (String userName : userList.keySet()) {
            datas.add(userName);
        }
        // 设置数据
        mSv_username.setAdapter(new SpinnerAdapter(context, datas));
    }


    /**
     * 初始化View
     */
    private void initViews() {
        // 找到View
        mBtn_login = findViewById(R.id.btn_login);
        mBtn_reg = findViewById(R.id.btn_one_key_reg);
        mSv_username = findViewById(R.id.spinner_view);
        mEt_username = mSv_username.findViewById(R.id.et_input);
        mEt_password = findViewById(R.id.et_pwd);
        mTv_forgetPwd = findViewById(R.id.hd_login_forget_pwd_textView);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_login) {
            doLogin();
        } else if (i == R.id.btn_one_key_reg) {
            getRoleInfo();
        } else if (i == R.id.hd_login_forget_pwd_textView) {

        }
    }

    /**
     * 登录
     */
    private void doLogin() {
        String url = API.GAME_LOGIN + "&username=" + mEt_username.getText().toString() + "&password=" + mEt_password.getText().toString();
        Utils.log(LoginDialog.class, "登录校验URL：" + url);
        mOkHttpHelper.get(url, new HttpCallback(mContext, "登录中") {
            @Override
            public void onSuccess(JSONObject json) {
                // 处理注册成功之后的事情
                dealWithSuccess(json, Const.EVENT_LOGIN);
            }

            @Override
            public void onError(JSONObject json) {
                ToastUtils.showErrorToast(mContext, json, null);
                // 回调数据给CP
                mLoginListener.onLoginFail(json);
            }
        });
    }


    /**
     * 获取注册信息
     */
    private void getRoleInfo() {

        mOkHttpHelper.get(API.GAME_ROLE_INFO, new HttpCallback(mContext, "注册中") {
            @Override
            public void onSuccess(JSONObject json) {
                // 处理获取注册信息之后
                dealWithSuccess(json, Const.EVENT_GET_ROLE_INFO);

            }

            @Override
            public void onError(JSONObject json) {
                ToastUtils.showErrorToast(mContext, json, null);

            }
        });
    }


    /**
     * 处理回调成功后的事情
     *
     * @param json
     */
    private void dealWithSuccess(JSONObject json, String event) {

        try {
            if (json.getString("code").equals("1")) {

                if (event.equals(Const.EVENT_LOGIN)) {
                    dealWithlogSuccess(json);
                } else if (event.equals(Const.EVENT_GET_ROLE_INFO)) {
                    dealWithgetRoleSuccess(json);
                } else if (event.equals(Const.EVENT_REGISTER)) {
                    dealWithRegisterSuccess(json);
                }

            } else {
                ToastUtils.showErrorToast(mContext, json, null);
                mLoginListener.onLoginFail(json);
                sendFailBroadcast();
            }
        } catch (JSONException e) {
            ToastUtils.showErrorToast(mContext, null, e.getMessage());
        }

    }


    private void sendSuccessBroadcast() {
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_LOGIN_STATE);
        intent.putExtra(Const.ISLOGIN, Const.SUCCESS);
        mContext.sendBroadcast(intent);

    }

    private void sendFailBroadcast() {
        // 发送登录失败的广播
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_LOGIN_STATE);
        intent.putExtra(Const.ISLOGIN, Const.FAIL);
        mContext.sendBroadcast(intent);
    }


    /**
     * 注册成功后
     *
     * @param json
     */
    private void dealWithRegisterSuccess(JSONObject json) {
        Utils.log(LoginDialog.class, "注册成功返回的信息：" + json);
        // 把用户信息存到本地中
        UserList.addUser(new String[]{mEt_username.getText().toString().trim(), psw}, mContext);
    }


    /**
     * 获取用户注册信息之后，应该去调用注册接口
     *
     * @param json
     */
    private void dealWithgetRoleSuccess(JSONObject json) {
        try {
            String username = json.getJSONObject("result").getString("username");
            psw = json.getJSONObject("result").getString("password");
            // 先设置到界面上，防止加载网络的速度太慢，导致无法较快显示到用户眼前
            mEt_username.setText(username);
            mEt_password.setText(psw);
            // 明文显示密码
            mEt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            String url = API.GAME_REGISTER + "&username=" + username + "&password=" + psw;
            Utils.log(LoginDialog.class, "注册的url:" + url);
            mOkHttpHelper.get(url, new HttpCallback(mContext, "注册中") {
                @Override
                public void onSuccess(JSONObject json) {
                    dealWithSuccess(json, Const.EVENT_REGISTER);
                }

                @Override
                public void onError(JSONObject json) {
                    ToastUtils.showErrorToast(mContext, json, null);

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 登录成功后
     *
     * @param json
     */
    private void dealWithlogSuccess(JSONObject json) {

        // 把用户信息存到本地中。因为注册和登录的，不一定是同一个玩家
        UserList.addUser(new String[]{mEt_username.getText().toString(), mEt_password.getText().toString()}, mContext);
        // 登录框消失
        closdDialog();
        // 回调给CP
        mLoginListener.onLoginSuccess(json);

        //显示悬浮窗
        // mFloatViewService.showFloatView();

        // 发送登录成功的广播
        sendSuccessBroadcast();

        // 保存数据
        // TODO 是否要绑定手机或者邮箱 !!!
        try {
            JSONObject result = json.getJSONObject("result");
            // 顺便存进SP中
            HDApplication.access_token = result.getString("access_token");
            PreferencesUtils.putString(mContext, Const.ACCESS_TOKEN, HDApplication.access_token);

            // 是否有绑定手机
            String phone = result.getString("phone");
            if (phone.equals("null")) {
                //Toast.makeText(mContext, "去绑定手机啦", Toast.LENGTH_SHORT).show();
                BindMobileTipDialog bindMobileTipDialog = new BindMobileTipDialog(mContext, mEt_username.getText().toString().trim());
                bindMobileTipDialog.show();
            }else {
                BindPhoneUser.addBindUser(mEt_username.getText().toString().trim(),phone);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
