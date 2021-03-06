package com.ming.npm.repository.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ming.npm.repository.filter.MyFilter;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        String newJson = replaceUrl(json, MyFilter.BASE_NPM_REGISTRY);
        super.responseFromLocalRepository(newJson.getBytes("utf-8"), response);
    }

    @Override
    protected void responseFromRemoteResource(String path, HttpServletResponse response) throws IOException {
        String npmUrl = MyFilter.BASE_NPM_REGISTRY + path;
        System.out.println("remote:=======>" + npmUrl);

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

        String newJson = replaceUrl(jsonResult, MyFilter.BASE_NPM_REGISTRY);

        // 自动下载所有版本的tar文件
        handleTarFile(newJson);

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

    /***
     * 替换资源路径为代理地址路径
     * @param json
     * @param replacement
     * @return
     */
    private String replaceUrl(String json, String replacement) {
//        String newJson = json.replaceAll(MyFilter.BASE_NPM_REGISTRY, getProxyUrl());

        StringBuffer sb = new StringBuffer();
        StringBuffer temp = new StringBuffer();

        char[] chars = json.toCharArray();
        for (char a : chars) {
            if (replacement.startsWith(String.valueOf(a)) || temp.length() > 0) {
                temp.append(a);
            } else {
                sb.append(a);
            }

            if (temp.length() > 0) {
                if (temp.length() != replacement.length() && !replacement.startsWith(String.valueOf(temp))) {
                    sb.append(temp);
                    temp.setLength(0);// clear content
                } else if (temp.toString().equalsIgnoreCase(replacement)) {
                    sb.append(getProxyUrl());
                    temp.setLength(0);// clear content
                }
            }
        }

        if (replacement.toLowerCase().startsWith("https")) {
            return replaceUrl(sb.toString(), "http" + replacement.substring(5));
        } else {
            return sb.toString();
        }
    }

    /**
     * 自动下载所有版本的tar文件
     * @param json
     */
    private void handleTarFile(String json) {
        try {
            List<String> allTarball = getAllTarball(json);

            for (int i = allTarball.size() - 1, count = 0; i >= 0 && count < 5; i--, count++)
                downLoadFile(allTarball.get(i));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getAllTarball(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        JsonObject versions = jsonObject.getAsJsonObject("versions");
        List<String> collect = versions.entrySet().stream().map(a ->
                a.getValue().getAsJsonObject().getAsJsonObject("dist").getAsJsonPrimitive("tarball").getAsString()
        ).collect(Collectors.toList());

        return collect;
    }

    private void downLoadFile(String url) {
        CloseableHttpClient httpclient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy()) // adds HTTP REDIRECT support to GET and POST methods
                .build();
        try {
            HttpGet get = new HttpGet(url); // we're using GET but it could be via POST as well
            CloseableHttpResponse execute = httpclient.execute(get);
            StatusLine statusLine = execute.getStatusLine();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
