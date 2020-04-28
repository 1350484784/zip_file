package com.cs.file.zip.service;

import java.util.List;

public interface FileService {

    /**
     * 下载json文件, 保存
     * @param token
     * @param type
     * @return
     */
    String downloadJson(String token, String type);

    /**
     * 根据type获取zip文件
     * @param type
     * @return
     */
    List<String> getDataPath(String type);
}
