package com.morningstar.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@Slf4j
public class ClassUtil {
    private final static String rootClassFileName = "Root.class";

    public static List<Class<?>> loadClassesByPackageName(String packageName) {
        String packagePath = packageName.replace('.', '/');

        // NOTE: 下面一系列繁琐的操作都是为了解决自测中会先查找到test-classes而不是classes下的字节码文件

        // 将路径转换为URL
        URL rootClassUrl = Thread.currentThread().getContextClassLoader().getResource(packagePath + "/" + rootClassFileName);
        if (rootClassUrl == null) {
            throw new RuntimeException(String.format("%s不存在，或其路径下没有Root类", packageName));
        }

        List<String> classNameList;
        if (rootClassUrl.getProtocol().equals("file")) {
            try {
                // 根据root文件找到主classpath上的路径
                File directory = new File(rootClassUrl.toURI()).getParentFile();
                // 遍历目录，找到所有的字节码名
                classNameList = Arrays
                        .stream(Objects.requireNonNull(directory.listFiles((dir, name) -> name.endsWith(".class"))))
                        .map((file) -> packageName + '.' + file.getName().replace(".class", ""))
                        .toList();
            } catch (URISyntaxException e) {
                classNameList = new ArrayList<>();
            }
        } else {
            classNameList = new ArrayList<>();

            String jarPath = rootClassUrl.getPath().substring(7, rootClassUrl.getPath().indexOf("!"));

            try (JarFile jarFile = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();

                    if (entryName.contains(packagePath) && entryName.endsWith(".class")) {
                        String shortClassName = entryName.substring(("BOOT-INF/classes/" + packagePath).length() + 1, entryName.length() - ".class".length());
                        classNameList.add(packageName + '.' + shortClassName);
                    }
                }
            } catch (IOException e) {
                return new ArrayList<>();
            }
        }

        // 读取字节码名为Class<?>
        return classNameList.stream()
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        // 打印错误信息，但继续扫描其他类
                        log.error("Error loading class {}: {}", className, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static Map<Class<?>, Object> loadInstancesByPackageName(Class<?> parentClass, String packageName) {
        List<Class<?>> classes = loadClassesByPackageName(packageName);
        Map<Class<?>, Object> instances = new HashMap<>();
        for (Class<?> clazz : classes) {
            // 检查是否是指定父类的子类
            if (parentClass.isAssignableFrom(clazz)) {
                // 创建类的实例【如果没有空参构造会报错】
                Object instance;
                try {
                    instance = clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException("无法通过无参构造创建类的实例");
                }
                // 将实例添加到Map中
                instances.put(clazz, instance);
            }
        }
        return instances;
    }
}