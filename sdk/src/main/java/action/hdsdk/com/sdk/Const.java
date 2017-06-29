package action.hdsdk.com.sdk;

/**
 * Created by shake on 2017/6/20.
 * 一些常量
 */
public class Const {

    public static final String INIT_SUCCESS = "success";
    public static final String INIT_FAIL = "fail";

    public static final int ORIENTATION = 5555;


    public static final String ERROR_TIP_LOGIN = "初始化失败，无法登录 !";
    public static final String ERROR_TIP_PAY = "用户还没登录，无法支付 !";
    public static final String ERROR_HAS_LOGIN = "用户已经处于登录状态 !";


    public static final String EVENT_LOGIN = "login";
    public static final String EVENT_GET_ROLE_INFO = "getInfo";
    public static final String EVENT_REGISTER = "register";

    // 用户集合
    public static final String USER_LIST = "userlist";
    public static final String LASR_USER = "last";

    // 绑定手机的用户集合
    public static final String BIND_PHONE_USER = "bind_phone_user";

    // 当前帐号
    public static final String CURRENT_USER = "current_user";

    // 手机号码
    public static final String PHONE = "phone";

    // 绑定手机
    public static final String BIND_PHONE = "bind_phone";

    // 检查验证码
    public static final String CHECK_AUTH_CODE = "check_code";

    // 重新获取验证码
    public static final String REGET_AUTH_CODE = "reget_authcode";

    // 服务端重新设置key
    public static final String RESET_KEY = "reset_key";

    // 重置密码
    public static final String RESET_PSD = "reset_psd";

    // access_token
    public static final String ACCESS_TOKEN = "access_token";

    // 对话框的标识
    public static final String LOGIN_DIALOG = "login_dialog";
    public static final String AUTO_LOGIN_DIALOG = "auto_login_dialog";
    public static final String BIND_PHONE_DIALOG = "bind_phone_dialog";
    public static final String ORDER_DIALOG = "order_dialog";

    // 自动登录返回数据
    public static final String AUTO_LOGIN_CALLBACK = "auto_login_callback";

    // 创建订单
    public static final String CREATE_ORDER = "create_order";

    // 检查订单
    public static final String CHECK_ORDER = "check_order";

    // 获取微信支付的参数
    public static final String GET_WX_PARAMS = "get_wx_params";

    // 支付回调
    public static final String PAY_RESULT= "pay_result";
    public static final String PAY_SUCCESS = "pay_success";
    public static final String PAY_FAIL = "pay_fail";
    public static final String PAY_CANCLE = "pay_fail";
    public static final String SUCCESS  = "success";
    public static final String FAIL = "fail";
    public static final String CANCLE = "cancle";
    public static final String ACTION_PAY = "action_pay";
    public static final String PAY_FAIL_INFO = "pay_fail_info";


    // 是否是登录状态
    public static final String ACTION_LOGIN_STATE = "login_state";
    public static final String ISLOGIN = "islogin";
    public static final String ISNOTLOGIN = "isnotlogin";


    // 是否是注销
    public static final String ACTION_LOGOUT = "action_logout";


    // 微信支付Activiy的回调
    public static final int WX_CALLBACK = 5;
    // 微信支付成功
    public static final int WX_PAY_SUCCESS = 100;
    // 微信支付失败
    public static final int WX_PAY_FAIL = 99;


    /**
     * 微信支付渠道
     */
    public static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    public static final String CHANNEL_ALIPAY = "alipay";

    /**
     * 退出对话框
     */
    public static final String EXIT_DIALOG_TITLE = "退出";
    public static final String EXIT_DIALOG_MESSAGE = "您确定要退出吗？";


    /**
     * 切换帐号对话框
     */
    public static final String LOGOUT_DIALOG_TITLE = "切换帐号";
    public static final String LOGOUT_DIALOG_MESSAGE = "您确定要切换帐号吗？";


    /**
     * 取消订单对话框
     */
    public static final String CANCLE_ORDER_DIALOG_TITLE = "取消订单";
    public static final String CANCLE_ORDER_DIALOG_MESSAGE = "您确定要取消订单？";


    /**
     * 下单相关信息
     */
    public static final String ORDER_INFO = "order_info";
    public static final String ORDER_PRODUCT_NAME = "productName";
    public static final String ORDER_AMOUNT = "amount";
    public static final String ORDER_NOTIFYURL = "notifyUrl";
    public static final String ORDER_EX_ORDERNUM = "exOrderNum";
    public static final String ORDER_ROLEID = "roleId";
    public static final String ORDER_SERVERID = "serverId";
    public static final String ORDER_EX_INFO = "exinfo";
    public static final String ORDER_PROCUCT_INFO = "productInfo";


    // 退出成功回调的内容
    public static final String EXIT_SUCCESS = "exit";
    // 取消退出回调的内容
    public static final String EXIT_CANCLE = "cancle";
    // 注销账户回调的内容
    public static final String LOGOUT_SUCCESS = "logout";

    // 加密串
    public static final String s = "VXS%2FAMTCla40exDJfQNAwsz%2Fyj0b7Kt3oA1vbthQ6f7UsQpC6ojQEkfQJQDV+Z5I5x%2FBFUN34xhuDnTmWJSXvzAiStzXMgXjrf1%2BzHjUhwBnPUOTxtuTl1y4G+LMtrAUpLeVd4qG7E42nJdEJEB2bqes3kaNVw%2Fp%2FqAapsa9t0oytFYHRFbd0y+UurWrR1Ky1A6QSid2oyn%2BPQ%2FD9MS2LW7PNU6opLz96PKKjkP0LK9bgyZ%2B7qq+ZESTlyYdehfEED6L83b3Gb%2B%2BIVUo%2B%2Bo2AFHeyLxi0O98VmPYOQbCiSj12%2B54+7osRvdY670k6je4dfHmiAt2nPp%2FvV3nUCRiWN6j6Xfe%2FgyF77rAc3vcK68Ed+v7TCz5GIXp03ZG%2BR%2BPAGWI84ehHzHmuJwPCxFSW7eoZ3SEhoPhIEWnyNx0Jr+ZkLCcxy3CAIYOnKzMuFODDf3%2Bh4848ftuS%2F39N%2Fe%2BYmtmaCZ4pO23qP7r8%2B8+z5Ta8wM%2B9zUkU7%2Fd7PDn8DVokPVZKO5fCK6f9w1OKzvSLuiBghBSUHon9d0L+L0fDOHO1xmzkkE52JWHEyyd8cuT9EM5IeEvV7qjslTzJdlcsWZiaSDa6X3Vz+UKznHQXqoGR0ec2TReBc9PkChYVc%2B0wDN4evEwzpiJthyCf0r7gxeH4ed1zf+M0vhscvGKCl4";
}
