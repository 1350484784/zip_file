package com.cs.file.zip.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/download")
public class DownloadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

    @GetMapping("/zipFile")
    public void getByTenantId(@RequestParam("type") String type, @RequestParam("token") String token) {
        LOGGER.info("token={}", token);
        LOGGER.info("type={}", type);

        // 获取token 下载
    }
}
