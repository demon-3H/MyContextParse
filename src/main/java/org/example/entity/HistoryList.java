package org.example.entity;


import org.example.entity.RoleContent;

import java.util.ArrayList;
import java.util.List;


/**
 * 存储历史对话的类
 *
 * @author guoshuai
 */
public class HistoryList {
    public List<RoleContent> historyList=new ArrayList<>();

    /**
     * 记录user回答的内容
     *
     * @param str
     */
    public void  user_store(String str){
        RoleContent roleContent=new RoleContent();
        roleContent.setRole("user");
        roleContent.setContent(str);
        historyList.add(roleContent);
    }

    /**
     * 记录assistant回答的内容
     *
     * @param str
     */
    public void  assistant_store(String str){
        RoleContent roleContent=new RoleContent();
        roleContent.setRole("assistant");
        roleContent.setContent(str);
        historyList.add(roleContent);
    }

    /**
     * 删除记录
     *
     */
    public void delete(){
        historyList.remove(historyList.size()-1);
        historyList.remove(historyList.size()-1);
    }
}
