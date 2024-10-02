package com.morningstar.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ClassUtil {
    public static List<Class<?>> loadClassesByPackagePath(String packagePath) {
        // NOTE: 下面一系列繁琐的操作都是为了解决测试中会先查找test-classes而不是classes下的字节码文件

        // 将路径转换为URL
        URL rootUrl = Thread.currentThread().getContextClassLoader().getResource(packagePath.replace(".", "/") + "/Root.class");
        if (rootUrl == null) {
            throw new RuntimeException(String.format("%s不存在，或其路径下没有Root类", packagePath));
        }
        try{
            // 根据root文件找到主classpath上的路径
            File directory = new File(rootUrl.toURI()).getParentFile();
            // 遍历目录，找到所有的字节码文件，并读取为Class<?>
            return Arrays
                    .stream(Objects.requireNonNull(directory.listFiles((dir, name) -> name.endsWith(".class"))))
                    .map((file)->{
                        String className = packagePath + '.' + file.getName().replace(".class", "");
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
        } catch (URISyntaxException e) {
            return new ArrayList<>();
        }
    }

    public static Map<Class<?>, Object> loadInstancesByPackagePath(Class<?> parentClass, String packagePath) {
        List<Class<?>> classes = loadClassesByPackagePath(packagePath);
        Map<Class<?>, Object> instances = new HashMap<>();
        for(Class<?> clazz : classes) {
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