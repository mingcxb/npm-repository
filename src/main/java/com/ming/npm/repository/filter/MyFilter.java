package com.ming.npm.repository.filter;

import com.ming.npm.repository.handler.RPMRequestHandler;
import com.ming.npm.repository.handler.RequestHandlerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class MyFilter implements Filter {

    public static final String BASE_NPM_REGISTRY = "https://registry.npmjs.org";
//    public static final String BASE_NPM_REGISTRY = "http://registry.npm.taobao.org";

    private Set<String> set = new HashSet<>();
    public static String HOME_DIR;
    public static String REPOSITORY_DIR;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        set.add("/");
        set.add("index");
        set.add("/favicon.ico");


        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource("");
        HOME_DIR = resource.getFile();
        REPOSITORY_DIR = HOME_DIR + "repository";
        System.out.println("REPOSITORY_DIR:" +  REPOSITORY_DIR);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        String url = request.getRequestURI();
        System.out.println("url==" + url);

        if (set.contains(url)) {
            // 本站请求
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // rpm 请求
            RPMRequestHandler handler = RequestHandlerFactory.getHandler(url);
            handler.handlerRequest(request, response);
        }
    }

    @Override
    public void destroy() {

    }

}
