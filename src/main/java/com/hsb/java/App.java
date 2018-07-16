package com.hsb.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static HashMap<String, Integer> classesData = new HashMap<>();
    private static HashMap<String, Integer> annotationsData = new HashMap<>();

    public static void main(String[] args) {
        String path = "D:/develop/github/spring-boot-examples";

        List<String> files = new ArrayList<>(getAllFilesPath(path));
        if (files.size() > 0) {
            for (String filePath : files) {
                if (filePath != null && (filePath.endsWith(".java")||filePath.endsWith(".xml"))) {
                    try {
                        String txt = getFileContent(filePath);
                        if (txt.contains("@Test")) {
                            System.out.println(filePath);
                        }
                        parseFileContent(txt);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        System.out.println("--------------------注解使用统计--------------------");
        System.out.println("共计使用注解" + annotationsData.size() + "个");
        showStatisticsData(annotationsData);
        System.out.println("--------------------类使用统计----------------------");
        System.out.println("共计使用类" + classesData.size() + "个");
        showStatisticsData(classesData);
    }

    private static void showStatisticsData(HashMap<String, Integer> data) {
        List<Map.Entry<String, Integer>> annotationLists = new ArrayList<>(data.entrySet());
        Collections.sort(annotationLists, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (Map.Entry<String, Integer> entry : annotationLists) {
            System.out.println(fillWithBlank(entry.getKey(), entry.getValue()));
        }
    }

    private static String fillWithBlank(String key, Integer value) {
        StringBuilder sb = new StringBuilder(key);
        int blanks = 100 - sb.length();
        for (int i = 0; i < blanks; i++) {
            sb.append(" ");
        }
        sb.append(value);
        return sb.toString().replace("import ", "").replace(";", "");
    }

    private static void parseFileContent(String txt) {
        String annotationRegex = "@[A-Z](\\w*)";
        findKeyword(txt, annotationRegex, annotationsData);
        String classRegex = "(import )[A-Za-z0-9_.]*\\;";
        findKeyword(txt, classRegex, classesData);
    }

    private static void findKeyword(String txt, String regex, Map<String, Integer> map) {
        Pattern pattern = Pattern.compile(regex);
        if (txt != null && txt.length() > 0) {
            Matcher matcher = pattern.matcher(txt);
            if (matcher.find()) {
                if (matcher.groupCount() > 0) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        String temp = matcher.group(i);
                        if (map.containsKey(temp)) {
                            map.put(temp, map.get(temp) + 1);
                        } else {
                            if (temp.length() > 1) {
                                map.put(temp, 1);
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getFileContent(String filePath) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    fileContent.append(line);
                    fileContent.append("\r\n");
                }
            } finally {
                bufferedReader.close();
                inputStreamReader.close();
            }
        }
        return fileContent.toString();
    }

    private static List<String> getAllFilesPath(String path) {
        List<String> result = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File temp : files) {
                if (temp.isDirectory()) {
                    result.addAll(getAllFilesPath(temp.getAbsolutePath()));
                } else {
                    result.add(temp.getAbsolutePath());
                }
            }
        }
        return result;
    }
}