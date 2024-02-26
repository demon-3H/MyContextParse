package org.example.utils.AIUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.example.entity.RoleContent;
import org.example.entity.HistoryList;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 科大讯飞大模型
 *
 * @author guoshuai
 */
@UtilityClass
@Slf4j
public class SparkAIUtil {

    //获取权限的网页
    public static final String hostUrl = "https://spark-api.xf-yun.com/v3.5/chat";

    //获取权限所需要的配置
    public static  String appid = "";
    public static  String apiSecret = "";
    public static  String apiKey = "";

    //用于解析对象为json
    public static final Gson gson = new Gson();

    public void init(String id,String secret,String key){
        appid=id;
        apiSecret=secret;
        apiKey=key;
    }
    /**
     * 用于调用大模型接口,解析文本
     *
     * @param str
     * @param historyList
     * @return string
     * @throws Exception
     */
    public String chat(String str, HistoryList historyList,String cmd) throws Exception {
        String auth = getAuthUrl(hostUrl, apiKey, apiSecret);
        String url = auth.toString().replace("http://", "ws://").replace("https://", "wss://");
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();
        BigModelNew bigModelNew = new BigModelNew(str+cmd,historyList);
        //发送请求
        WebSocket webSocket = client.newWebSocket(request, bigModelNew);
        do {
            Thread.sleep(200);
        } while (!bigModelNew.wsCloseFlag);
        String tmp=historyList.historyList.get(historyList.historyList.size()-1).getContent();
        historyList.delete();
        webSocket.close(1000,"1");
        return tmp;
    }

    /**
     * 鉴权方法
     *
     * @param hostUrl
     * @param apiKey
     * @param apiSecret
     * @return string
     * @throws Exception
     */
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }

    class BigModelNew extends WebSocketListener{
        private String question = "";
        public StringBuffer stringBuffer = new StringBuffer();
        private Boolean wsCloseFlag=false;
        private HistoryList historyList;
        public BigModelNew(String question,HistoryList historyList){
            this.question=question;
            this.historyList=historyList;
        }
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            log.info("创建连接");
            JSONObject requestJson=new JSONObject();
            JSONObject header=new JSONObject();  // header参数
            header.put("app_id",appid);

            JSONObject parameter=new JSONObject(); // parameter参数
            JSONObject chat=new JSONObject();
            chat.put("domain","generalv3.5");
            chat.put("temperature",0.5);
            chat.put("max_tokens",4096);
            parameter.put("chat",chat);

            JSONObject payload=new JSONObject(); // payload参数
            JSONObject message=new JSONObject();
            JSONArray text=new JSONArray();

            historyList.user_store(question);
            // 历史问题获取
            if(historyList.historyList.size()>0){
                for(RoleContent tempRoleContent:historyList.historyList){
                    text.add(JSON.toJSON(tempRoleContent));
                }
            }
            message.put("text",text);
            payload.put("message",message);
            requestJson.put("header",header);
            requestJson.put("parameter",parameter);
            requestJson.put("payload",payload);
            webSocket.send(requestJson.toString());
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            log.info("连接关闭");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
            if (myJsonParse.header.code != 0) {
                log.info("发生错误，错误码为：" + myJsonParse.header.code);
                log.info("本次请求的sid为：" + myJsonParse.header.sid);
                webSocket.close(1000, "");
            }
            List<Text> textList = myJsonParse.payload.choices.text;

            for (Text temp : textList) {
                stringBuffer.append(temp.content);
            }
            if (myJsonParse.header.status == 2) {
                log.info("回答: "+stringBuffer);
                // 回答
                historyList.assistant_store(new String(stringBuffer));
                stringBuffer=new StringBuffer();
                wsCloseFlag = true;
            }

        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
        }
    }

    //返回的json结果拆解
    class JsonParse {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        int status;
        String sid;
    }

    class Payload {
        Choices choices;
    }

    class Choices {
        List<Text> text;
    }

    class Text {
        String role;
        String content;
    }
}