package com.morningstar.infra.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CopyUtil {
    public static void copyNonNullProperties(Object source, Object target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("源对象或目标对象为空");
        }

        for (Field sourceField : allFields(source.getClass())) {
            sourceField.setAccessible(true);
            Object value;
            try {
                value = sourceField.get(source);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (value != null) {
                // 沿父类链按名字找 target 的字段
                Field targetField = findField(target.getClass(), sourceField.getName());
                if (targetField == null) {
                    // target 没有这个字段,跳过
                    continue;
                }
                try {
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static List<Field> allFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    // static/编译器生成字段不属于实例数据,不拷
                    continue;
                }
                if (!seen.contains(field.getName())) {
                    // 同名遮蔽时只留子类的
                    seen.add(field.getName());
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static Field findField(Class<?> clazz, String name) {
        while (clazz != null && clazz != Object.class) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
