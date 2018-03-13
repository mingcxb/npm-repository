package com.ming.npm.repository.handler;

import com.ming.npm.repository.ApplicationContextProvider;
import org.apache.commons.lang3.StringUtils;

public class RequestHandlerFactory {

    public static RPMRequestHandler getHandler(String path) {
        if (StringUtils.isEmpty(path))
            return null;

        RPMRequestHandler handler;
        // 使用本地仓库代理其它需要下载的资源
        // 如：安装node-sass库是，需要下载文件：https://github.com/sass/node-sass/release/download/v4.8.1/win32-x64-59_binding.node
        // 首先使用命令设置sass文件代理：npm config set sass_binary_site http://localhost:3000/my-mirrors/node-sass
        // 并将需要的.node文件放到对应的仓库目录下
        if (path.startsWith("/my-mirrors")) {
            handler = ApplicationContextProvider.getBean(OtherFileRequestHandler.class);
        } else if (path.endsWith(".tgz"))
            handler = ApplicationContextProvider.getBean(TgzRequestHandler.class);
        else
            handler = ApplicationContextProvider.getBean(JsonRequestHandler.class);

        return handler;
    }

}
