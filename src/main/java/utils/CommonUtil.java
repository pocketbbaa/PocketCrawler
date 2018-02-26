package utils;

import annotation.PageFieldSelect;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基本工具
 * Created by wangyang on 2018/1/11.
 */
public class CommonUtil {

    private static final String URL_REGEX = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * map非空判断
     *
     * @param map
     * @return
     */
    public static boolean mapIsNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * url格式校验
     */
    public static boolean isUrl(String url) {
        return url != null && url.trim().length() > 0 && url.startsWith("http");
    }

    /**
     * url正则校验
     *
     * @param str
     * @return
     */
    public static boolean regexUrl(String str) {
        return !(str == null || str.trim().length() == 0) && matches(URL_REGEX, str);
    }

    public static boolean matches(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    /**
     * 参数解析 （支持：Byte、Boolean、String、Short、Integer、Long、Float、Double、Date）
     *
     * @param field
     * @param value
     * @return
     */
    public static Object parseValue(Field field, String value) {

        Class<?> fieldType = field.getType();

        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType fieldGenericType = (ParameterizedType) field.getGenericType();
            if (fieldGenericType.getRawType().equals(List.class)) {
                Type gtATA = fieldGenericType.getActualTypeArguments()[0];
                fieldType = (Class<?>) gtATA;
            }
        }

        PageFieldSelect apiRequestParam = field.getAnnotation(PageFieldSelect.class);

        if (value == null || value.trim().length() == 0)
            return null;
        value = value.trim();

        if (Byte.class.equals(fieldType) || Byte.TYPE.equals(fieldType)) {
            return parseByte(value);
        }
        if (Boolean.class.equals(fieldType) || Boolean.TYPE.equals(fieldType)) {
            return parseBoolean(value);
        }
        if (String.class.equals(fieldType)) {
            return value;
        }
        if (Short.class.equals(fieldType) || Short.TYPE.equals(fieldType)) {
            return parseShort(value);
        }
        if (Integer.class.equals(fieldType) || Integer.TYPE.equals(fieldType)) {
            return parseInt(value);
        }
        if (Long.class.equals(fieldType) || Long.TYPE.equals(fieldType)) {
            return parseLong(value);
        }
        if (Float.class.equals(fieldType) || Float.TYPE.equals(fieldType)) {
            return parseFloat(value);
        }
        if (Double.class.equals(fieldType) || Double.TYPE.equals(fieldType)) {
            return parseDouble(value);
        }
        if (Date.class.equals(fieldType)) {
            return parseDate(apiRequestParam, value);
        }
        throw new RuntimeException("【属性解析错误】属性必须是包装类型 = " + fieldType);

    }

    public static Byte parseByte(String value) {
        try {
            value = value.replaceAll("　", "");
            return Byte.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseByte but input illegal input=" + value, e);
        }
    }

    public static Boolean parseBoolean(String value) {
        value = value.replaceAll("　", "");
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        } else {
            throw new RuntimeException("parseBoolean but input illegal input=" + value);
        }
    }

    public static Integer parseInt(String value) {
        try {
            value = value.replaceAll("　", "");
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseInt but input illegal input=" + value, e);
        }
    }

    public static Short parseShort(String value) {
        try {
            value = value.replaceAll("　", "");
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseShort but input illegal input=" + value, e);
        }
    }

    public static Long parseLong(String value) {
        try {
            value = value.replaceAll("　", "");
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseLong but input illegal input=" + value, e);
        }
    }

    public static Float parseFloat(String value) {
        try {
            value = value.replaceAll("　", "");
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseFloat but input illegal input=" + value, e);
        }
    }

    public static Double parseDouble(String value) {
        try {
            value = value.replaceAll("　", "");
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseDouble but input illegal input=" + value, e);
        }
    }

    public static Date parseDate(PageFieldSelect apiRequestParam, String value) {
        try {
            String datePattern = "yyyy-MM-dd HH:mm:ss";
            if (apiRequestParam != null) {
                datePattern = apiRequestParam.datePattern();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException("parseDate but input illegal input=" + value, e);
        }
    }

}
