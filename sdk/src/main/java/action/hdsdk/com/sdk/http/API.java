package action.hdsdk.com.sdk.http;



/**
 * Created by xk on 2017/6/20.
 */
public class API {

    // 用户初始化接口，在这个接口调用之后，成功之后，弹出登录框。现在暂时用梦幻遮天的参数
    public static final String GAME_SETTING = "http://api.hdg123.cn/index.php?r=game/setting&package_id=mhzt_01&game_id=585";
    //public static final String GAME_SETTING = "http://api.hdg123.cn/index.php?r=game/setting&package_id=qmsj_01&game_id=572";

    // 服务器分配角色信息
    public static final String GAME_ROLE_INFO = "http://api.hdg123.cn/index.php?r=user/rand&game_id=585&package_id=mhzt_01";

    // 向服务器注册
    public static final String GAME_REGISTER = "http://api.hdg123.cn/index.php?r=user/register&game_id=585&package_id=mhzt_01";

    // 用户登录接口
    public static final String GAME_LOGIN = "http://api.hdg123.cn/index.php?r=auth/authorize&UnallowToke=true&game_id=585&package_id=mhzt_01";

    // 发送手机验证码
    public static final String GAME_VERIFY_CONE = "http://api.hdg123.cn/index.php?r=user/phoneresetkey&game_id=585&package_id=mhzt_01";

    // 绑定手机
    public static final String GAME_BIND_PHONE = "http://api.hdg123.cn/index.php?r=user/binding&type=phone&game_id=585&package_id=mhzt_01";

    // 检查验证码
    public static final String GAME_CHECK_AUTHCODE = "http://api.hdg123.cn/index.php?r=user/checkresetkey&type=PHONE&game_id=585&package_id=mhzt_01";

    // 重新去给手机设置发送验证码
    public static final String GAME_RESET_KEY= "http://api.hdg123.cn/index.php?r=user/resetKey&type=USERNAME&game_id=585&package_id=mhzt_01";

    // 重置密码
    public static final String GAME_RESET_PSD = "http://api.hdg123.cn/index.php?r=user/resetPassword&type=phone&game_id=585&package_id=mhzt_01";

    // 自动登录
    public static final String GAME_AUTO_LOGIN = "http://api.hdg123.cn/index.php?r=auth/checkaccesstoken&game_id=585&package_id=mhzt_01";

    // 创建订单
    public static final String GAME_CREATE_ORDER = "http://api.hdg123.cn/index.php?r=order/create";

    // 检查订单
    public static final String GAME_CHECK_ORDER = "http://api.hdg123.cn/index.php?r=order/check&game_id=585&package_id=mhzt_01";

    // 请求微信支付的参数
    //public static final String GAME_GET_WECHAT_PARAMS = "http://api.hdg123.cn/index.php?r=order/paylink&payment=wftwx&order_no=5950a95bed011&type=android&game_id=571&package_id=qmsj_01";
    public static final String GAME_GET_WECHAT_PARAMS = "http://api.hdg123.cn/index.php?r=order/paylink&payment=wftwx&type=android&game_id=585&package_id=mhzt_01";

}
