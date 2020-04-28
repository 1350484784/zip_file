package com.cs.file.zip.service.impl;

import com.cs.file.zip.config.FileTypeConfiguration;
import com.cs.file.zip.service.FileService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    @Value("${file.json.path}")
    private String fileJsonPath;

    @Value("${file.data.path}")
    private String fileDatePath;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String downloadJson(String token, String type) {
        String url = fileJsonPath + "?token=" + token + "&type=" + type;

        HttpHeaders headers = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET,
                httpEntity, byte[].class);

        String path = null;
        FileOutputStream fos = null;
        try {
            File file = File.createTempFile("gui-config", ".json");
            fos = new FileOutputStream(file);
            path = file.getPath();
            fos.write(response.getBody());
            fos.flush();

            LOGGER.info("文件下载成功，路径：{}", path);
        } catch (Exception e) {
            LOGGER.error("文件下载失败：{}", e.getMessage());
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error("文件流关闭，异常：{}", e.getMessage());
                }
            }
        }

        return path;
    }

    @Override
    public List<String> getDataPath(String type) {
        List<String> paths = new ArrayList<>();
        String path = fileDatePath + FileTypeConfiguration.getFileTpyeMap().get(type);

//        String path = Objects
//                .requireNonNull(Objects.requireNonNull(ClassUtils.getDefaultClassLoader()).getResource(pathType)).getPath();
        try {
            traverseFolder(URLDecoder.decode(path, "UTF-8"), paths);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }
        return paths;
    }


    private void traverseFolder(String path, List<String> paths) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                return;
            } else {
                for (File file2 : files) {
                    try {
                        paths.add(new String(file2.getAbsolutePath().getBytes("UTF-8")));
                    } catch (UnsupportedEncodingException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        }
    }
}
