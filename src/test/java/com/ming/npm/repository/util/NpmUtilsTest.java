package com.ming.npm.repository.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class NpmUtilsTest {

    @Test
    public void testPath() {
        String s1 = "file:/C:/Users/Ming/IdeaProjects/npm-repository/target/npm-repository-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/";
        if (s1.indexOf(".jar!") != -1) {
            s1 = s1.substring(0, s1.indexOf(".jar!"));
            s1 = s1.substring(0, s1.lastIndexOf("/"));
        }

        System.out.println(s1);
    }

}