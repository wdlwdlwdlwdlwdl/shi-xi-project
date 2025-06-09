package com.aliyun.gts.gmall.manager.front.login.dto.utils;

import java.util.regex.Pattern;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年10月17日 14:31:00
 */
public class PassWordUtils {

    /**
     * 特殊字符
     */
    public static final String SPEC_CHARACTERS = " !\"#$%&'()*+,-./:;<=>?@\\]\\[^_`{|}~";

    /**
     * 字母和数字
     */
    public static final String NUMBER_AND_CHARACTER = "((^[a-zA-Z]{1,}[0-9]{1,}[a-zA-Z0-9]*)+)" +
            "|((^[0-9]{1,}[a-zA-Z]{1,}[a-zA-Z0-9]*)+)$";


    public static boolean checkPassword(String targetString) {
        String opStr = targetString;
        char[] charArray = opStr.toCharArray();
        for (char c : charArray) {
            if (SPEC_CHARACTERS.contains(String.valueOf(c))) {
                opStr = opStr.replace(c, ' ');
            }
        }
        String excSpecCharStr = opStr.replace(" ", "");
        return Pattern.compile(NUMBER_AND_CHARACTER).matcher(excSpecCharStr).matches();
    }

}
