package org.example.entity;


import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.objenesis.instantiator.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;


/**
 * 存储不同对话历史的类
 *
 * @author guoshuai
 */
@Data
public class Role {

    private HistoryList historyList=new HistoryList();

    public Role(){
    }

    public Role(String fileName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        InputStream inputStream =classPathResource.getInputStream();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String s;
        int tag=1;
        while ((s=bufferedReader.readLine())!=null){
            if(tag%2!=0){
                historyList.user_store(s);
            }
            else {
                historyList.assistant_store(s);
            }
            tag++;
        }
    }
}
