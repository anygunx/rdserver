package com.lg.util;

import org.w3c.dom.Element;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XingYun on 2016/5/11.
 */
public class ReflectionUtil {
    /**
     * 获取字段的装箱类型
     * 此方法只能处理基本类型
     *
     * @param object
     * @param columnName
     * @return
     * @throws NoSuchFieldException
     */
    public static final Class getFieldBoxedClass(Object object, String columnName) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(columnName.trim());
        if (field == null) {
            System.out.println("Field " + columnName + "is not found in object " + object);
            return null;
        }
        Type fieldType = field.getGenericType();
        return getBoxedClassByType(fieldType);
    }

    /**
     * 是否基本类型
     *
     * @param clz
     * @return
     */
    public static boolean isPrimitiveClass(Class clz) {
        if (clz.isPrimitive()) {
            return true;
        }
        Class wrapClass = getWrapClass(clz);
        return wrapClass != null && wrapClass.isPrimitive();
    }

    public static Class getWrapClass(Class clz) {
        try {
            return ((Class) clz.getDeclaredField("TYPE").get(null));
        } catch (Exception e) {
            return null;
        }
    }

    public static final Class getUnBoxClass(Type type) throws NoSuchFieldException {
        return getUnBoxClass(type.toString().replace("class ", ""));
    }

    public static final Class getUnBoxClass(String className) throws NoSuchFieldException {
        switch (className) {
            case "java.lang.Integer":
            case "int":
                return int.class;
            case "java.lang.Byte":
            case "byte":
                return byte.class;
            case "java.lang.Short":
            case "short":
                return short.class;
            case "java.lang.Long":
            case "long":
                return long.class;
            case "class java.lang.Float":
            case "float":
                return float.class;
            case "java.lang.Double":
            case "double":
                return double.class;
            case "java.lang.Boolean":
            case "boolean":
                return boolean.class;
//            case "java.lang.Character":
//            case "char":
//                return char.class;
            default:
                return null;
        }
    }

    public static Method getSetter(Class clazz, String columnName, Class paramClass) throws NoSuchMethodException {
        String name = "set" + StringUtil.toFirstUpper(columnName);
        Method setter = clazz.getMethod(name, paramClass);
        if (setter == null) {
            System.out.println("Method " + name + "is not found in class " + clazz);
            return null;
        }
        return setter;
    }

    public static Method getGetter(Class clazz, String columnName) throws NoSuchMethodException {
        Method getter = clazz.getMethod("get" + StringUtil.toFirstUpper(columnName));
        return getter;
    }


    public static String getFieldsStringValue(Object data, Field field) {
        Class clazz = data.getClass();
        try {
            Method getter = getGetter(clazz, field.getName());
            Class fieldClass = getBoxedClassByType(field.getGenericType());
            Object value = getter.invoke(data);
            if (fieldClass != null && isPrimitiveClass(fieldClass)) { // 基本类型
                // 获取装箱类型
                Method toString = getBoxedClass(fieldClass.getName()).getDeclaredMethod("toString", getUnBoxClass(fieldClass.getName()));
                return (String) toString.invoke(data, value);
            } else {    // 其他类型都用string赋值
                return value == null ? "''" : "'" + value + "'";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <E> E createByElement(Class<E> clazz, Element element) throws Exception {
        E data = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class fieldClass = getBoxedClassByType(field.getGenericType());
            if (fieldClass != null && isPrimitiveClass(fieldClass)) { // 基本类型
                Method setter = getSetter(clazz, field.getName(), getUnBoxClass(fieldClass.getName()));
                // 获取装箱类型
                Method valueOf = getBoxedClass(fieldClass.getName()).getDeclaredMethod("valueOf", String.class);
                String value = XmlUtils.getAttribute(element, field.getName());
                setter.invoke(data, valueOf.invoke(null, value));
            } else {    // 其他类型都用string赋值
                Method setter = getSetter(clazz, field.getName(), String.class);
                setter.invoke(data, XmlUtils.getAttribute(element, field.getName()));
            }
        }
        return data;
    }

    /**
     * 为本类中声明的属性生成形如 field1=value1,field2=value2 的字符串表示
     * 注：不包括父类
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String getFieldsString(Object data) throws Exception {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        Class clazz = data.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            index++;
            Class fieldClass = getBoxedClassByType(field.getGenericType());
            String valueStr = null;
            if (fieldClass != null && isPrimitiveClass(fieldClass)) { // 基本类型
                Method getter = getGetter(clazz, field.getName());
                valueStr = getter.invoke(data).toString();
            } else {    // 其他类型都用string赋值
                Method getter = getGetter(clazz, field.getName());
                valueStr = getter.invoke(data).toString();
            }
            builder.append(field.getName()).append("=").append(valueStr);
            if (index != fields.length) {
                builder.append(",");
            }
        }
        return builder.toString();
    }


    /**
     * 使用形如field1=value1;field2=value2 的字符串初始化fields
     *
     * @param data
     * @param string
     */
    public static <E> void initFieldsWithString(E data, String string) throws Exception {
        Class clazz = data.getClass();
        Map<String, Field> allFields = getAllFields(clazz);
        String[] params = string.split(",");
        for (String str : params) {
            String[] entry = str.split("=");
            Field field = allFields.get(entry[0]);
            Class fieldClass = getBoxedClassByType(field.getGenericType());
            if (fieldClass != null && isPrimitiveClass(fieldClass)) { // 基本类型
                Method setter = getSetter(data.getClass(), field.getName(), getUnBoxClass(fieldClass.getName()));
                // 获取装箱类型
                Method valueOf = fieldClass.getDeclaredMethod("valueOf", String.class);
                setter.invoke(data, valueOf.invoke(null, entry[1]));
            } else {    // 其他类型都用string赋值
                Method setter = getSetter(data.getClass(), field.getName(), String.class);
                // 容错
                String value = entry.length < 2 ? "" : entry[1];
                setter.invoke(data, value);
            }
        }
    }

    public static Map<String, Field> getAllFields(Class clazz) {
        Map<String, Field> allFields = new HashMap<>();
        do {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                allFields.put(field.getName(), field);
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return allFields;
    }

    public static final Class getBoxedClassByType(Type type) throws NoSuchFieldException {
        return getBoxedClass(type.toString().replace("class ", ""));
    }

    /**
     * 获取基本类型的装箱类型
     *
     * @param className
     * @return
     */
    private static Class getBoxedClass(String className) {
        switch (className) {
            case "java.lang.Integer":
            case "int":
                return Integer.class;
            case "java.lang.Byte":
            case "byte":
                return Byte.class;
            case "java.lang.Short":
            case "short":
                return Short.class;
            case "java.lang.Long":
            case "long":
                return Long.class;
            case "java.lang.Float":
            case "float":
                return Float.class;
            case "java.lang.Double":
            case "double":
                return Double.class;
            case "java.lang.Boolean":
            case "boolean":
                return Boolean.class;
            case "java.lang.Character":
            case "char":
                return Character.class;
            default:
                return null;
        }
    }


    public static String getFieldDefaultSQLType(Type genericType) {
        switch (genericType.toString()) {
            case "class java.lang.Integer":
            case "int":
                return "int(11)";
            case "class java.lang.Byte":
            case "byte":
                return "tinyint(4)";
            case "class java.lang.Short":
            case "short":
                return "smallint(11)";
            case "class java.lang.Long":
            case "long":
                return "bigint(22)";
            case "class java.lang.Float":
            case "float":
                return "float(10,0)";
            case "class java.lang.Double":
            case "double":
                return "double(10,0)";
            case "class java.lang.Boolean":
            case "boolean":
                return "tinyint(1)";
            case "class java.lang.Character":
            case "char":
                return "char(1)";
            case "class java.lang.String":
                return "varchar(255)";
            default:
                return null;
        }
    }


    public static String getFieldsStringValueWithoutQuote(Object data, Field field) {
        Class clazz = data.getClass();
        try {
            Method getter = getGetter(clazz, field.getName());
            Class fieldClass = getBoxedClassByType(field.getGenericType());
            Object value = getter.invoke(data);
            if (fieldClass != null && isPrimitiveClass(fieldClass)) { // 基本类型
                // 获取装箱类型
                Method toString = getBoxedClass(fieldClass.getName()).getDeclaredMethod("toString", getUnBoxClass(fieldClass.getName()));
                return (String) toString.invoke(data, value);
            } else {    // 其他类型都用string赋值
                return value == null ? "" : value + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
//        System.out.println(isPrimitiveClass(String.class));
//        System.out.println(isPrimitiveClass(void.class));
//        System.out.println(isPrimitiveClass(int.class));
//        System.out.println(isPrimitiveClass(Integer.class));
//        System.out.println(isPrimitiveClass(ReflectionUtil.class));

//        Field[] fields = new ResCDKey().getClass().getDeclaredFields();
//        for (Field field : fields) {
//            System.out.println(field.getClass());
//            System.out.println(field.getDeclaringClass());
//            System.out.println(field.getType());
//            System.out.println(field.getGenericType());
//        }

    }
}
