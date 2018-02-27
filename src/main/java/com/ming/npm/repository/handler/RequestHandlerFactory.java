package com.ming.npm.repository.handler;

import com.ming.npm.repository.ApplicationContextProvider;
import org.apache.commons.lang3.StringUtils;

public class RequestHandlerFactory {

    public static RPMRequestHandler getHandler(String path) {
        if (StringUtils.isEmpty(path))
            return null;

        RPMRequestHandler handler;
        if (path.endsWith(".tgz"))
            handler = ApplicationContextProvider.getBean(TgzRequestHandler.class);
        else
            handler = ApplicationContextProvider.getBean(JsonRequestHandler.class);

        return handler;
    }

}
