package com.cs.file.zip.config;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileTypeConfiguration {

    private static Map<String, String> fileTpyeMap = new HashMap<>(4);

    public static Map<String, String> getFileTpyeMap() {
        return fileTpyeMap;
    }

    @PostConstruct
    public void initProperties(){
        fileTpyeMap.put("ssr-win_10", "win10");
        fileTpyeMap.put("v2rayN-win", "v2rayN");
        fileTpyeMap.put("ssr-win_7-64", "win7_64");
        fileTpyeMap.put("ssr-win_7-32", "win7_32");
    }
}
