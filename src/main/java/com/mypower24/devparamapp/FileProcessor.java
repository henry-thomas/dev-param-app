/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.devparamapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author henry
 */
public class FileProcessor {

    private static final Logger LOG = Logger.getLogger(FileProcessor.class.getName());

    public static Map<String, DevParamSetting> processFileAuto(File file) {
        String name = file.getName();
        if (name.endsWith(".java")) {
            Map<String, DevParamSetting> fileMap = processJavaFile(file);
            return fileMap;
        }
        return null;
    }

    public static Map<String, DevParamSetting> processJavaFile(File file) {
        List<DevParamSetting> settingList = new ArrayList<>();
        Map<String, DevParamSetting> settingMap = new HashMap<>();

        try ( FileInputStream fis = new FileInputStream(file)) {
            LOG.info("Processing java file");

            List<String> readFileLines = readFileLines(file.getPath());
            StringBuilder sb = new StringBuilder();

            for (String readFileLine : readFileLines) {
                String trim = readFileLine.trim();
                if (trim.startsWith("private")) {

                    String[] split = trim.split(" ");
                    DevParamSetting field = handlePrivateField(split);
                    settingMap.put(field.getParamName(), field);
                    settingList.add(field);
//                    sb.append(readFileLine).append("\n");
                }
            }

            LOG.info(sb.toString());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return settingMap;
    }

    private static DevParamSetting handlePrivateField(String[] split) {
        String type = split[1];
        DevParamSetting devParamSetting = new DevParamSetting();
        switch (type) {
            case "int":
            case "double":
            case "Double":
            case "Integer":
            case "long":
            case "float":
            case "short":
                devParamSetting.setType("inputNumber");
                break;
            case "String":
                devParamSetting.setType("inputText");
                break;
            case "Boolean":
            case "boolean":
                devParamSetting.setType("switch");
                break;
            default:
                LOG.log(Level.WARNING, "{0} is not accepted type", type);
        }

        String paramName = null;
        if (split[2].contains("//")) {
            int indexOf = split[2].indexOf("//");

            if (indexOf != -1) {
                paramName = split[2].substring(0, indexOf);
            }

        } else {
            paramName = split[2];
        }

        String finalName = null;
        if (paramName != null && paramName.contains(";")) {
            finalName = paramName.replace(";", "");
        } else {
            finalName = paramName;
        }
        devParamSetting.setParamName(finalName);

        return devParamSetting;
    }

    public static void processJsonFile(File file) {
        try ( FileInputStream fis = new FileInputStream(file)) {
            LOG.info("Processing JSON file");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static List<String> readFileLines(String fd) {
        if (Files.isReadable(new File(fd).toPath())) {
            try {
                Stream<String> lines = Files.lines(new File(fd).toPath());
                List<String> collect = lines.collect(Collectors.toList());
                return collect;
            } catch (IOException ex) {
                Logger.getLogger(FileProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

}
