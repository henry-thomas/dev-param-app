/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mypower24.devparamapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public static final String FILE_BACKUP_SUFIX = "_backup";

    public static Map<String, DevParamSetting> processFileAuto(File file) {
        String name = file.getName();
        if (name.endsWith(".java")) {
            Map<String, DevParamSetting> fileMap = processJavaFile(file);
            return fileMap;
        }
        if (name.endsWith(".js") || name.endsWith(".json")) {
            return processJsonFile(file);
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

    public static Map<String, DevParamSetting> processJsonFile(File file) {
        Map<String, DevParamSetting> map = new HashMap<>();

        TypeReference<HashMap<String, DevParamSetting>> typeRef = new TypeReference<HashMap<String, DevParamSetting>>() {
        };
        
        readJsonObjectFromFileIntoObject(file.getPath(), map, typeRef);
        return map;
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

    public static boolean saveObjectToFileAsJson(Object object, String file) {
        return saveObjectToFileAsJson(object, file, false, false);
    }

    public static boolean saveObjectToFileAsJson(Object object, String filePathStr, boolean writeWithBackup, boolean append) {
        File f = new File(filePathStr);
        try {
            Files.createDirectories(f.getParentFile().toPath());
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FileProcessor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        if (writeWithBackup) {
            if (Files.isReadable(f.toPath())) {
                copyFile(filePathStr, filePathStr + FILE_BACKUP_SUFIX);
            }
        }

        try ( FileWriter fw = new FileWriter(f, append)) {
            ObjectMapper objectMapper = new ObjectMapper();
            //for prity print
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            String strJson = objectMapper.writeValueAsString(object);

            fw.write(strJson);
        } catch (Exception e) {
            return false;
        }

        if (writeWithBackup) {
            copyFile(filePathStr, filePathStr + FILE_BACKUP_SUFIX);
        }

        return true;
    }

    public static boolean copyFile(String src, String dst) {
        Path copied = Paths.get(dst);
        Path originalPath = Paths.get(src);
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static boolean readJsonObjectFromFileIntoObject(String file, Object destination) {
        return readJsonObjectFromFileIntoObject(file, destination, null);
    }

    public static boolean readJsonObjectFromFileIntoObject(String file, Object destination, TypeReference typeRef) {
        if (!Files.exists(Paths.get(file)) && Files.exists(Paths.get(file + FILE_BACKUP_SUFIX))) {
            copyFile(file + FILE_BACKUP_SUFIX, file);
        }

        if (Files.exists(Paths.get(file))) {
            String jsonContent = FileProcessor.readFileAsString(file);
            if (jsonContent != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);

                    JsonNode rootNode = objectMapper.readTree(jsonContent.getBytes());
                    if (rootNode == null) {
                        return false;
                    }
                    if (typeRef != null) {
                        objectMapper.readerForUpdating(destination).readValue(rootNode.traverse(), typeRef);
                    } else {
                        objectMapper.readerForUpdating(destination).readValue(rootNode.traverse());
                    }
                    return true;

                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(FileProcessor.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        saveObjectToFileAsJson(destination, file);
        return false;
    }

    public static byte[] readFile(String filePath) {
        try ( FileInputStream input = new FileInputStream(filePath)) {
            // load a properties file
            byte b[] = new byte[input.available()];
            input.read(b, 0, b.length);
            return b;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String readFileAsString(String filePath) {
        return new String(readFile(filePath));
    }

}
