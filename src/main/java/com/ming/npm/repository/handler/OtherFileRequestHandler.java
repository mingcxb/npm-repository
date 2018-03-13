package com.ming.npm.repository.handler;

import com.ming.npm.repository.filter.MyFilter;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Component
// 使用本地仓库代理其它需要下载的资源
// 如：安装node-sass库是，需要下载文件：https://github.com/sass/node-sass/release/download/v4.8.1/win32-x64-59_binding.node
// 首先使用命令设置sass文件代理：npm config set sass_binary_site http://localhost:3000/my-mirrors/node-sass
// 并将需要的.node文件放到对应的仓库目录下
public class OtherFileRequestHandler extends RPMRequestHandler {

    @Override
    protected void responseFromLocalRepository(String path, HttpServletResponse response) throws IOException {
        response.setContentType("Content-Type: application/octet-stream");
        super.responseFromLocalRepository(path, response);
    }

    @Override
    protected void responseFromRemoteResource(String path, HttpServletResponse response) throws IOException {
        String npmRepositoryServer = remoteUrl(path);
        System.out.println("remote:=======>" + npmRepositoryServer);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(npmRepositoryServer);
        CloseableHttpResponse response1 = httpClient.execute(get);


//        Header[] allHeaders = response1.getAllHeaders();
//        Arrays.stream(allHeaders).forEach(a -> {
//            System.out.println("\"" + a.getName() + "\", \"" + a.getValue() + "\"");
//        });

        HttpEntity entity = response1.getEntity();
        byte[] bytes = EntityUtils.toByteArray(entity);
        response1.close();
        httpClient.close();

        saveTgzFile(MyFilter.REPOSITORY_DIR + path, bytes);

        response.setContentLength(bytes.length);
        response.setContentType(String.valueOf(entity.getContentType()));
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    private String remoteUrl(String path) {

        if (path.indexOf("node-sass") != -1) {
            String[] split = path.split("/");
            String version = split[split.length - 2];
            String name = split[split.length - 1];
            return "https://github.com/sass/node-sass/release/download/" + version + "/" + name;
        } else {
            return "";
        }
    }

    /**
     * 保存tgz文件
     * @param path
     * @param bytes
     * @throws IOException
     */
    private void saveTgzFile(String path, byte[] bytes) throws IOException {
        File tgzFile = new File(path);

        File dir = new File(tgzFile.getParent());

        if (!dir.exists())
            dir.mkdirs();

        if (!tgzFile.exists())
            tgzFile.createNewFile();

        FileUtils.writeByteArrayToFile(tgzFile, bytes);
    }
}
