package com.cs.file.zip.controller;

import com.cs.file.zip.service.FileService;
import com.cs.file.zip.utils.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/download")
public class DownloadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

    @Autowired
    private FileService fileService;

    @Value("${file.zip.path}")
    private String fileZipPath;

    @GetMapping("/zipFile")
    @ResponseBody
    public void getByTenantId(@RequestParam("type") String type,
            @RequestParam("token") String token, HttpServletResponse response) {
        LOGGER.info("token={}", token);
        LOGGER.info("type={}", type);

        response.setHeader("Content-Type", "application/octet-stream;charset=utf-8");
        // 获取token 下载json
        String path = fileService.downloadJson(token, type);
        // zip 路径
        String zipPath;

        String fileSuffix = "zip";

        if (path != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("json文件保存路径：{}", path);
            }
            File jsonFile = new File(path);
            if(!jsonFile.exists() || jsonFile.length() == 0) {
                LOGGER.error("json文件内容为空");
                new File(path).delete();
                return;
            }

            // 根据type 获取初始文件
            List<String> paths = fileService.getDataPath(type);

            if (!CollectionUtils.isEmpty(paths)) {
                // 进行压缩
                paths.add(path);
                zipPath = fileZipPath + type + "." + fileSuffix;

                // 文件不存在，新建
                File file = new File(zipPath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage());
                    }
                }

                synchronized (DownloadController.class){
                    ZipUtil.zip(paths, zipPath, type);

                    LOGGER.info("压缩文件路径：{}", zipPath);

                    // 网络下载
                    // FileSystemView fsv = FileSystemView.getFileSystemView();
                    // File com=fsv.getHomeDirectory();
                    // try {
                    //    ZipUtil.downloadFile(zipPath, com.getPath() + type + fileSuffix);
                    // } catch (Exception e) {
                    //    LOGGER.error(e.getMessage());
                    // }
                    // 本地下载
                    Path zipNioPath = Paths.get(zipPath);

                    response.setContentType("application/" + fileSuffix);
                    response.addHeader("Content-Disposition", "attachment;filename="+type + "." + fileSuffix);
                    try {
                        Files.copy(zipNioPath, response.getOutputStream());
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage() );
                    }
                }
            }

            new File(path).delete();
        }


    }


    @RequestMapping("/index")
    public String logout() {
        LOGGER.info("index");
        return "html/index";
    }
}
