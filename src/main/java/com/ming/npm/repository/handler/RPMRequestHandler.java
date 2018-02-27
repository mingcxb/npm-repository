package com.ming.npm.repository.handler;

import com.ming.npm.repository.filter.MyFilter;
import org.apache.commons.io.FileUtils;
import org.apache.coyote.http11.Http11InputBuffer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public abstract class RPMRequestHandler {

    public void handlerRequest(HttpServletRequest request, HttpServletResponse response ) throws IOException {
        String url = request.getRequestURI();

        if (exists(url)) {
            responseFromLocalRepository(url, response);
        } else {
            String newUrl = handleUrl(url);
            responseFromRemoteResource(newUrl, response);
        }
    }

    protected String handleUrl(String url) {
        // 把@重新转义，
        if (url.lastIndexOf(Http11InputBuffer.AT) > 1) {
            String temp = url.substring(2, url.length()).replaceAll(Http11InputBuffer.AT, Http11InputBuffer.AT_CHAR);
            if (url.indexOf(Http11InputBuffer.AT) == 1){
                return "/" + Http11InputBuffer.AT + temp;
            } else {
                return "/" + temp;
            }
        }
        return url;
    }

    protected String getFilePath(String path) {
        String temp = path.replaceAll(Http11InputBuffer.AT_CHAR, Http11InputBuffer.AT);
        if (temp.lastIndexOf(Http11InputBuffer.AT) > 1) {
            String[] split = temp.split(Http11InputBuffer.AT);
            return MyFilter.REPOSITORY_DIR + File.separator + Http11InputBuffer.AT + split[1] + File.separator + split[2];
        } else {
            return MyFilter.REPOSITORY_DIR + path;
        }
    }

    protected boolean exists(String path) throws IOException {
        String filePath = getFilePath(path);
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
