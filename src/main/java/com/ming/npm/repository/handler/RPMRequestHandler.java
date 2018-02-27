package com.ming.npm.repository.handler;

import com.ming.npm.repository.filter.MyFilter;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public abstract class RPMRequestHandler {

    public void handlerRequest(HttpServletRequest request, HttpServletResponse response ) throws IOException {
        String url = request.getRequestURI();
        String newUrl = handleUrl(url);

        if (exists(newUrl)) {
            responseFromLocalRepository(newUrl, response);
        } else {
            System.out.println("remote:=======>" + newUrl);
            responseFromRemoteResource(newUrl, response);
        }
    }

    protected String handleUrl(String url) {
//        if (url.indexOf("@") != -1) {
//            return url.substring(0, url.lastIndexOf("@"));
//        }
        return url;
    }

    protected String getFilePath(String path) {
        return  MyFilter.REPOSITORY_DIR + path;
    }

    protected boolean exists(String path) throws IOException {
        String filePath = MyFilter.REPOSITORY_DIR + path;
        File file = new File(filePath);
        return file.exists();
    }

    protected void responseFromLocalRepository(String path, HttpServletResponse response) throws IOException {
        String filePath = getFilePath(path);
        byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
        response.setContentLength(bytes.length);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    protected void responseFromLocalRepository(byte[] bytes, HttpServletResponse response) throws IOException {
        response.setContentLength(bytes.length);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    protected abstract void responseFromRemoteResource(String path, HttpServletResponse response) throws IOException;

}
