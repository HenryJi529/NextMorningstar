package com.morningstar.kill.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClassScanner {
    public static File[] scanPackagePath(String packagePath) {
        // NOTE: 下面一系列繁琐的操作都是为了解决测试中会先查找test-classes而不是classes下的字节码文件

        // 将路径转换为URL
        URL rootUrl = Thread.currentThread().getContextClassLoader().getResource(packagePath.replace(".", "/") + "/Root.class");
        if (rootUrl == null) {
            throw new RuntimeException(String.format("%s不存在，或其路径下没有Root类", packagePath));
        }
        try{
            // 根据root文件找到主classpath上的路径
            File directory = new File(rootUrl.toURI()).getParentFile();
            // 遍历目录
            return directory.listFiles((dir, name) -> name.endsWith(".class"));
        } catch (URISyntaxException e) {
            return new File[0];
        }
    }

    public static Map<Class<?>, Object> loadChildrenByPackagePath(Class<?> parentClass, String packagePath) {
        File[] files = scanPackagePath(packagePath);
        Map<Class<?>, Object> instances = new HashMap<>();
        for (File file : files) {
            // 构建完整的类名
            String className = packagePath + '.' + file.getName().replace(".class", "");
            try {
                // 加载类
                Class<?> clazz = Class.forName(className);
                // 检查是否是指定父类的子类
                if (parentClass.isAssignableFrom(clazz)) {
                    // 创建类的实例【如果没有空参构造会报错】
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    // 将实例添加到Map中
                    instances.put(clazz, instance);
                }
            } catch (Exception e) {
                // 打印错误信息，但继续扫描其他类
                log.error("Error loading class {}: {}", className, e.getMessage());
            }
        }
        return instances;
    }
}