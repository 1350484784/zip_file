package com.cs.file.zip.utils;

import com.cs.file.zip.service.impl.FileServiceImpl;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZipUtil {

    private static int BUFFERSIZE = 1024;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 压缩
     */
    public static void zip(List<String> paths, String fileName, String type) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(fileName));
            for (String filePath : paths) {
                // 递归压缩文件
                File file = new File(filePath);
                String relativePath = file.getName();
                if (relativePath.contains("gui-config")) {
                    if (type.equals("v2rayN-win")){
                        relativePath = relativePath.substring(0, relativePath.indexOf("gui-config"))
                                + "guiNConfig.json";
                    } else {
                        relativePath = relativePath.substring(0, relativePath.indexOf("gui-config"))
                                + "gui-config.json";
                    }
                }

                if (file.isDirectory()) {
                    relativePath += File.separator;
                }
                LOGGER.info("压缩文件：{}", relativePath);
                zipFile(file, relativePath, zos);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static void zipFile(File file, String relativePath, ZipOutputStream zos) {
        InputStream is = null;
        try {
            if (!file.isDirectory()) {

                ZipEntry zp = new ZipEntry(relativePath);
                zos.putNextEntry(zp);
                is = new FileInputStream(file);
                byte[] buffer = new byte[BUFFERSIZE];
                int length = 0;
                while ((length = is.read(buffer)) >= 0) {
                    zos.write(buffer, 0, length);
                }
                zos.flush();
                zos.closeEntry();
            } else {
                String tempPath = null;
                for (File f : file.listFiles()) {
                    tempPath = relativePath + f.getName();
                    if (f.isDirectory()) {
                        tempPath += File.separator;
                    }
                    zipFile(f, tempPath, zos);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * 网络文件下载
     */
    public static void downloadFile(String fileUrl, String fileLocal) throws Exception {

        URL url = new URL(fileUrl);
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.setConnectTimeout(6000);
        urlCon.setReadTimeout(6000);
        int code = urlCon.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败");
        }
        //读文件流
        DataInputStream in = new DataInputStream(urlCon.getInputStream());
        DataOutputStream out = new DataOutputStream(new FileOutputStream(fileLocal));
        byte[] buffer = new byte[2048];
        int count = 0;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        try {
            out.close();
            in.close();

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }
}
