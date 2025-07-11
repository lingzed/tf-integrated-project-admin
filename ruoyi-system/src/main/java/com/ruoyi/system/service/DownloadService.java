package com.ruoyi.system.service;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

@Service
public class DownloadService {

    /**
     * 下载文件，指定输出文件名
     * @param response      响应
     * @param file          待下载的文件
     * @param outFilename   输出的文件名
     * @throws IOException
     */
    public void download(HttpServletResponse response, File file, String outFilename) throws IOException {
        doDownload(response, file, outFilename);
    }

    /**
     * 下载文件，输出文件名为下载的文件名
     * @param response      响应
     * @param file          待下载的文件
     * @throws IOException
     */
    public void download(HttpServletResponse response, File file) throws IOException {
        doDownload(response, file, null);
    }

    /**
     * 下载
     * @param response      响应
     * @param file          待下载文件
     * @param outFilename   输出文件名，若为空，则取下载文件名
     * @throws IOException
     */
    private void doDownload(HttpServletResponse response, File file, String outFilename) throws IOException {
        response.setHeader("Content-Type", "application/octet-stream");
        FileUtils.setAttachmentResponseHeader(response, StringUtils.isEmpty(outFilename) ? file.getName() : outFilename);
        try (InputStream is = Files.newInputStream(file.toPath());
             OutputStream os = response.getOutputStream()) {
            byte[] bytes = new byte[1024];
            int len;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
        }
    }
}
