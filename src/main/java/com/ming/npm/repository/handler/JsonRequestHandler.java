package com.ming.npm.repository.handler;

import com.ming.npm.repository.filter.MyFilter;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Component
public class JsonRequestHandler extends RPMRequestHandler {

    private final String fileName = "package.json";

    @Value("${server.hostname}")
    private String hostname;
    @Value("${server.port}")
    private String port;

    private String getProxyUrl() {
        String porxyUrl = "http://" + hostname + ":" + port;
        return porxyUrl;
    }

    @Override
    protected String getFilePath(String path) {
        String filePath = super.getFilePath(path) + File.separator + fileName;
        return  filePath;
    }

    @Override
    protected void responseFromLocalRepository(String path, HttpServletResponse response) throws IOException {
        response.setContentType("Content-Type: application/json; charset=utf-8");

        String filePath = getFilePath(path);
        String json = FileUtils.readFileToString(new File(filePath));
        String newJson = json.replaceAll(MyFilter.BASE_NPM_REGISTRY, getProxyUrl());
        super.responseFromLocalRepository(newJson.getBytes("utf-8"), response);
    }

    @Override
    protected void responseFromRemoteResource(String path, HttpServletResponse response) throws IOException {
        String npmUrl = MyFilter.BASE_NPM_REGISTRY + path;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(npmUrl);
        CloseableHttpResponse response1 = httpClient.execute(get);

        HttpEntity entity = response1.getEntity();
        String jsonResult = EntityUtils.toString(entity);


        if ("{\"error\":\"Not found\"}".equals(jsonResult))  {
            System.out.println(npmUrl + "==>" + jsonResult);
        } else {
            saveJson(path, jsonResult);
        }

        String newJson = jsonResult.replaceAll(MyFilter.BASE_NPM_REGISTRY, getProxyUrl());
        byte[] bytes = newJson.getBytes("utf-8");

//        response.setContentLength(bytes.length);
//        response.setContentType("application/json; charset=UTF-8");

        Arrays.stream(response1.getAllHeaders()).forEach(a->{
            response.addHeader(a.getName(), a.getValue());
        });

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();

        response1.close();
        httpClient.close();
    }

    /**
     * 保存json描述文件
     * @param path
     * @param jsonStr
     * @throws IOException
     */
    private void saveJson(String path, String jsonStr) throws IOException {
        File dir = new File(super.getFilePath(path));
        if (!dir.exists())
            dir.mkdirs();

        File jsonFile = new File(getFilePath(path));
        if (!jsonFile.exists())
            jsonFile.createNewFile();

        FileUtils.writeStringToFile(jsonFile, jsonStr, "utf-8");
    }
}
