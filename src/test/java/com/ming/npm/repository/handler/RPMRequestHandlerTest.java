package com.ming.npm.repository.handler;

import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.*;

public class RPMRequestHandlerTest {

    @Test
    public void handleUrl() {

        String str = "@babel/code-frame@7.0.0-beta.31";

        RPMRequestHandler handler = new RPMRequestHandler() {
            @Override
            protected void responseFromRemoteResource(String path, HttpServletResponse response) throws IOException {

            }
        };

        String s = handler.handleUrl(str);
        System.out.println(s);

    }
}