package action.hdsdk.com.sdk.http;

import action.hdsdk.com.sdk.cyrpt.DES;
import action.hdsdk.com.sdk.utils.BaseInfo;

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



}
