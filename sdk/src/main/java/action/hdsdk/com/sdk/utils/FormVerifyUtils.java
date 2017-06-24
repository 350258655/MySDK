package action.hdsdk.com.sdk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Desction:表单判断
 * Author:pengjianbo
 * Date:15/7/21 下午8:13
 */
public class FormVerifyUtils {

    public static boolean checkUserName(String username) {
        if (StringUtils.isEmpty(username) || username.length() < 6 || username.length() > 16) {
            return  false;
        }
        return true;
    }

    public static boolean checkPassword(String password) {
        if (StringUtils.isEmpty(password) || password.length() < 6 || password.length() > 30) {
            return  false;
        }
        return true;
    }

    public static boolean checkMobile(String mobile) {
        try {
            if (mobile == null || mobile.length() != 11) {
                return false;
            } else {
                Pattern p = Pattern.compile("^[1][0-9]{10}$");
                Matcher m = p.matcher(mobile);
                return (m.matches());
            }
        } catch (PatternSyntaxException e) {
            return false;
        }
    }



    /**
     * 此方法判断输入字符是否为数字0-9 是返回true不是返回false
     *
     * @param c char
     * @return boolean
     */
    public static boolean isDigit(char c) {
        return (('0' <= c) && (c <= '9'));
    }


    /**
     * 此方法判断输入字符是否为字母a-z或A-Z 是返回true不是返回false
     *
     * @param c char
     * @return boolean
     */
    public static boolean isAlpha(char c) {
        return ((('a' <= c) && (c <= 'z')) || (('A' <= c) && (c <= 'Z')));
    }

    /**
     * 此方法用于检查密码或用户名是否合法，用户名密码只能使用英文字母、数字
     *
     * @param inputStr 输入
     * @return boolean
     */
    public static boolean checkUserNamePassword(String inputStr) {

        // 先校验长度
        if (StringUtils.isEmpty(inputStr) || inputStr.length() < 6 || inputStr.length() > 30) {
            return  false;
        }

        // 再校验是否是只含有字母和数字
        for (int nIndex = 0; nIndex < inputStr.length(); nIndex++) {
            char cCheck = inputStr.charAt(nIndex);

            if (!(isDigit(cCheck) || isAlpha(cCheck))) {
                return false;
            }
        }
        return true;
    }
}
