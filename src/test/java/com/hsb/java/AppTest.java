package com.hsb.java;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        File file = new File("D:/develop/github/spring-boot-examples/spring-boot-web-thymeleaf/src/test/java/com/neo/MessageControllerWebTests.java");
        StringBuilder fileContent = new StringBuilder();
        if (file.exists() && file.isFile()) {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    fileContent.append(line);
                    fileContent.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                bufferedReader.close();
                inputStreamReader.close();
            }
        }
//        String content = fileContent.toString();
//        System.out.println(content);
//        System.out.println("===================================================");
//        System.out.println();
//        String regex = "@[A-Z][A-Za-z0-9]*";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(content);
//        while (matcher.find()) {
//            System.out.println(matcher.group());
//        }
        Assert.assertTrue(true);
    }

    @Test
    public void stringRegexTest() {
        String classRegex = "[A-Z][A-Za-z0-9]*";
        String string = "@Test my OverTest or OveRTest RTEST";
        Pattern pattern = Pattern.compile(classRegex);
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
