package com.hsb.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        File file = new File("D:\\dev\\Git\\github\\spring-boot-examples\\spring-boot-web-thymeleaf\\src\\test\\java\\com\\neo\\MessageControllerWebTests.java");
        StringBuilder fileContent = new StringBuilder();
        if (file.exists() && file.isFile()) {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    fileContent.append(line);
                    fileContent.append(" ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                bufferedReader.close();
                inputStreamReader.close();
            }
        }
        String content = fileContent.toString();
        System.out.println(content);
        System.out.println();
        String regex = "^\\b[A-Z][A-Za-z0-9]*\\b$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (matcher.matches()) {
            for (int i =0; i < matcher.groupCount();i++) {
                System.out.println(matcher.group(i));
            }
        }
    }
}
