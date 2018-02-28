package com.ming.npm.repository.util;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class NpmUtils {

    public static String getAppPath(Class cls) throws UnsupportedEncodingException {
        java.net.URL url = cls.getProtectionDomain().getCodeSource().getLocation();
        String filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");

        java.io.File file = new java.io.File(filePath);
        filePath = file.getAbsolutePath();

        if (filePath.indexOf(File.separator + "file:") != -1)
            filePath = filePath.substring(0, filePath.indexOf(File.separator + "file:"));

        return filePath;
    }

}