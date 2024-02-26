package org.example.service;



/**
 * 业务解析接口
 *
 * @author guoshuai
 */
public interface TranslationService {


    /**
     * 业务识别解析
     *
     * @param str
     * @return string
     * @throws Exception
     */
    public String translationIdentification(String str) throws Exception;
}
