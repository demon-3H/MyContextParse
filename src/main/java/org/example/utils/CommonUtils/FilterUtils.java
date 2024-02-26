package org.example.utils.CommonUtils;

import cn.hutool.json.JSONObject;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 提取模板工具类
 *
 * @guoshuai
 */
@UtilityClass
@Slf4j
public class FilterUtils {

    /**
     * 提取json模板
     *
     * @param str
     * @return string
     */
    public String contextFilter(String str) {
        // 定义正则表达式，匹配花括号中的内容
        String regex = "\\*\\*([^)]*)\\*\\*";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(str);
        String match = null;
        // 查找匹配
        while (matcher.find()) {
            // 获取匹配到的内容，即花括号中的内容
            match = matcher.group(1);
            log.info("提取到的内容: " + match);
        }
        JSONObject jsonObject = new JSONObject(match);
        return jsonObject.toString();
    }
}
