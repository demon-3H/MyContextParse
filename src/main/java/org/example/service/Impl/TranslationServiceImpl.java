package org.example.service.Impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.config.RoleConfig;
import org.example.service.TranslationService;
import org.example.utils.AIUtils.SparkAIUtil;
import org.example.utils.CommonUtils.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 业务解析实现类
 *
 * @author guoshuai
 */
@Service
@Slf4j
public class TranslationServiceImpl implements TranslationService {

    @Autowired
    private RoleConfig roleConfig;


    /**
     * 业务识别解析
     *
     * @param str
     * @return string
     * @throws Exception
     */
    @Override
    public String translationIdentification(String str) throws Exception {

        System.out.println(roleConfig.getRole1().getHistoryList().historyList);
        String chat = SparkAIUtil.chat(str,roleConfig.getRole1().getHistoryList(),"，这句话属于刚刚我给你的5种业务的哪一种,将结果放在提前规定好的模板里面");
        String s = FilterUtils.contextFilter(normalize(chat));
        JSONObject jsonObject =  JSON.parseObject(s);
        String translation = jsonObject.getString("业务");

        if(translation.equals("查询业务")){
            String chat1 = SparkAIUtil.chat(str, roleConfig.getRole2().getHistoryList(),"，将这句话解析后放在提前规定好的模板里面");
            return FilterUtils.contextFilter(chat1);
        }
        else if (translation.equals("记录顾客档案")){
            String chat1 = SparkAIUtil.chat(str, roleConfig.getRole3().getHistoryList(),"，将这句话解析后放在提前规定好的模板里面");
            return FilterUtils.contextFilter(chat1);
        }
        else if (translation.equals("添加待办事项")){
            String chat1 = SparkAIUtil.chat(str, roleConfig.getRole4().getHistoryList(),"，将这句话解析后放在提前规定好的模板里面");
            return FilterUtils.contextFilter(chat1);
        }
        else if (translation.equals("记录预约")){
            String chat1 = SparkAIUtil.chat(str, roleConfig.getRole5().getHistoryList(),"，将这句话解析后放在提前规定好的模板里面");
//            return FilterUtils.contextFilter(chat1);
            return chat1;
        }
        return chat;
    }


    /**
     * 解析内容规范化
     *
     * @param s
     * @return string
     */
    public String normalize(String s){
        return s.replace("”","\"").replace("“","\"").replace("：",":");
    }

}
