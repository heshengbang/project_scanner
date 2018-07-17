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
import java.util.stream.Collectors;

public class App {
    private static HashMap<String, Integer> keywordsData = new HashMap<>();
    private static HashMap<String, Integer> annotationsData = new HashMap<>();
    private static HashMap<String, Integer> classImportedData = new HashMap<>();

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        String startInfo = "============================================= SCANNING START =================================================";
        outputAndAppend(sb, startInfo);


        String path = "D:/develop/github/spring-boot-examples";
//         String path = args[0];

        String pathInfo = "Path 【" + path + "】 will be scanned";
        outputAndAppend(sb, pathInfo);

        List<String> files = new ArrayList<>(getAllFilesPath(path));

        files = files.stream().filter(file -> file.endsWith(".java") || file.endsWith(".xml")).collect(Collectors.toList());

        String countInfo = "A total of " + files.size() + " files were scanned";
        outputAndAppend(sb, countInfo);

        if (files.size() > 0) {
            for (String filePath : files) {
                try {
                    String txt = getFileContent(filePath);
                    parseFileContent(txt);
                } catch (Exception ignored) {
                }
            }
        }
        cleanClassImportedData(classImportedData);
        deleteReplicateString(annotationsData, keywordsData);
        outputToConsoleAndLocal(sb);

        String endInfo = "============================================= SCANNING END ===================================================";
        outputAndAppend(sb, endInfo);

        writeToLocalFile(sb);
    }

    private static void outputAndAppend(StringBuilder sb, String info) {
        System.out.println(info);
        sb.append(info).append(Constants.NEWLINE);
    }


    private static void writeToLocalFile(StringBuilder sb) {
        File localFile = new File("statistics.txt");
        try {
            try (OutputStream output = new FileOutputStream(localFile)) {
                byte[] bytes = sb.toString().getBytes();
                output.write(bytes);
            }
        } catch (IOException e) {
            System.out.println("------------------------------ warning: output statistics to local file failed -------------------------------");
        }
    }

    private static void outputToConsoleAndLocal(StringBuilder sb) {

        String classesIllustration = "------------------------------------------- Classes Used Statistics ------------------------------------------";
        System.out.println(classesIllustration);
        sb.append(classesIllustration).append(Constants.NEWLINE);

        String classesStatistics = "Totally " + classImportedData.size() + " classes imported";
        System.out.println(classesStatistics);
        sb.append(classesStatistics).append(Constants.NEWLINE);

        showStatisticsData(classImportedData, sb, 120);
        sb.append(Constants.NEWLINE);


        String annotationIllustration = "----------------------------------------- Annotations Used Statistics ----------------------------------------";
        System.out.println(annotationIllustration);
        sb.append(annotationIllustration).append(Constants.NEWLINE);

        String annotationStatistics = "Totally " + annotationsData.size() + " annotations be used";
        System.out.println(annotationStatistics);
        sb.append(annotationStatistics).append(Constants.NEWLINE);

        showStatisticsData(annotationsData, sb, 50);
        sb.append(Constants.NEWLINE);


        String keywordsIllustration = "------------------------------------------ Keywords Used Statistics ------------------------------------------";
        System.out.println(keywordsIllustration);
        sb.append(keywordsIllustration).append(Constants.NEWLINE);

        String keywordsStatistics = "Totally " + keywordsData.size() + " keywords appeared";
        System.out.println(keywordsStatistics);
        sb.append(keywordsStatistics).append(Constants.NEWLINE);

        showStatisticsData(keywordsData, sb, 50);
        sb.append(Constants.NEWLINE);
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

        List<String> singleWord = new ArrayList<>();
        Set<String> keys = keywordsData.keySet();
        for (String key : keys) {
            if (key != null && key.length() == 1) {
                singleWord.add(key);
            }
        }
        for (String single : singleWord) {
            keywordsData.remove(single);
        }
    }

    private static void showStatisticsData(HashMap<String, Integer> data, StringBuilder sb, int stringLength) {
        List<Map.Entry<String, Integer>> annotationLists = new ArrayList<>(data.entrySet());
        annotationLists.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<String, Integer> entry : annotationLists) {
            String output = fillWithBlank(entry.getKey(), entry.getValue(), stringLength);
            System.out.println(output);
            sb.append(output).append(Constants.NEWLINE);
        }
    }

    private static String fillWithBlank(String key, Integer value, int stringLength) {
        StringBuilder sb = new StringBuilder(key);
        int blanks = stringLength - sb.length();
        for (int i = 0; i < blanks; i++) {
            sb.append(" ");
        }
        sb.append(value);
        return sb.toString().replace(Constants.IMPORT, Constants.EMPTY).replace(Constants.COLON, Constants.EMPTY);
    }

    private static void parseFileContent(String txt) {
        String annotationRegex = "@[A-Z][A-Za-z0-9]*";
        findKeyword(txt, annotationRegex, annotationsData);
        String classRegex = "import +[A-Za-z0-9\\.]*;";
        findKeyword(txt, classRegex, classImportedData);
        String keywordsRegex = "[A-Z][A-Za-z0-9]*";
        findKeyword(txt, keywordsRegex, keywordsData);
    }

    private static void cleanClassImportedData(HashMap<String, Integer> classImportedData) {
        Map<String, Integer> afterClean = new HashMap<>();
        Set<String> keys = classImportedData.keySet();
        for (String key : keys) {
            if (key != null && key.startsWith(Constants.IMPORT) && key.endsWith(Constants.COLON)) {
                String temp = key.replace(Constants.IMPORT, Constants.EMPTY);
                temp = temp.replace(Constants.COLON, Constants.EMPTY);
                temp = temp.trim();
                int value = classImportedData.get(key);
                afterClean.put(temp, value);
            }
        }
        classImportedData.clear();
        classImportedData.putAll(afterClean);
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