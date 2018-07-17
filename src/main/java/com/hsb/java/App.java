package com.hsb.java;

import com.hsb.java.constants.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static HashMap<String, Integer> classesData = new HashMap<>();
    private static HashMap<String, Integer> annotationsData = new HashMap<>();

    public static void main(String[] args) {
        String path = args[0];
        System.out.println("Begin to scan the path ：" + path + " ");

        List<String> files = new ArrayList<>(getAllFilesPath(path));
        if (files.size() > 0) {
            for (String filePath : files) {
                if (filePath != null && (filePath.endsWith(".java") || filePath.endsWith(".xml"))) {
                    try {
                        String txt = getFileContent(filePath);
                        parseFileContent(txt);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        deleteReplicateString(annotationsData, classesData);
        outputToConsoleAndLocal();
    }

    private static void outputToConsoleAndLocal() {
        StringBuilder sb = new StringBuilder();
        String annotationIllustration = "--------------------Annotations Used Statistics--------------------";
        System.out.println(annotationIllustration);
        sb.append(annotationIllustration).append(Constants.NEWLINE);

        String annotationStatistics = "Totally use annotations " + annotationsData.size();
        System.out.println(annotationStatistics);
        sb.append(annotationStatistics).append(Constants.NEWLINE);

        showStatisticsData(annotationsData, sb);

        String classesIllustration = "--------------------Keyword Used Statistics----------------------";
        System.out.println(classesIllustration);
        sb.append(classesIllustration).append(Constants.NEWLINE);

        String classStatistics = "Totally appeared keywords " + classesData.size();
        System.out.println(classStatistics);
        sb.append(classStatistics).append(Constants.NEWLINE);

        showStatisticsData(classesData, sb);

        File localFile = new File("statistics.txt");
        try {
            try (OutputStream output = new FileOutputStream(localFile)) {
                byte[] bytes = sb.toString().getBytes();
                output.write(bytes);
            }
        } catch (IOException e) {
            System.out.println("================== warning: output statistics to local file failed ========================");
        }
    }

    private static void deleteReplicateString(Map<String, Integer> annotations, Map<String, Integer> classes) {
        if (annotations != null && classes != null) {
            Set<String> keySet = annotations.keySet();
            for (String annotation : keySet) {
                if (annotation != null && annotation.startsWith(Constants.AT)) {
                    annotation = annotation.replace(Constants.AT, Constants.EMPTY);
                    classes.remove(annotation);
                }
            }
        }
    }

    private static void showStatisticsData(HashMap<String, Integer> data, StringBuilder sb) {
        List<Map.Entry<String, Integer>> annotationLists = new ArrayList<>(data.entrySet());
        annotationLists.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<String, Integer> entry : annotationLists) {
            String output = fillWithBlank(entry.getKey(), entry.getValue());
            System.out.println(output);
            sb.append(output).append(Constants.NEWLINE);
        }
    }

    private static String fillWithBlank(String key, Integer value) {
        StringBuilder sb = new StringBuilder(key);
        int blanks = 50 - sb.length();
        for (int i = 0; i < blanks; i++) {
            sb.append(" ");
        }
        sb.append(value);
        return sb.toString().replace("import ", Constants.EMPTY).replace(";", Constants.EMPTY);
    }

    private static void parseFileContent(String txt) {
        String annotationRegex = "@[A-Z][A-Za-z0-9]*";
        findKeyword(txt, annotationRegex, annotationsData);
        String classRegex = "[A-Z][A-Za-z0-9]*";
        findKeyword(txt, classRegex, classesData);
    }

    private static void findKeyword(String txt, String regex, Map<String, Integer> map) {
        Pattern pattern = Pattern.compile(regex);
        if (txt != null && txt.length() > 0) {
            Matcher matcher = pattern.matcher(txt);
            while (matcher.find()) {
                int beforeStart = matcher.start() - 1;
                if (beforeStart >= 0 && !Constants.AT.equals(String.valueOf(txt.charAt(beforeStart)))) {
                    String temp = matcher.group();
                    if (map.containsKey(temp)) {
                        int count = map.get(temp);
                        map.put(temp, count + 1);
                    } else {
                        map.put(temp, 1);
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
                    fileContent.append(Constants.NEWLINE);
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