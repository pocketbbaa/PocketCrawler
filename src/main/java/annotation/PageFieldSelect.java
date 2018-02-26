package annotation;

import enums.SelectType;

import java.lang.annotation.*;

/**
 * 属性注解，给属性赋值
 * Created by wangyang on 2018/2/24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface PageFieldSelect {


    /**
     * CSS选择器, 如 "#title"
     *
     * @return
     */
    public String cssQuery() default "";

    /**
     * jquery 数据抽取方式，如 ".html()/.text()/.val()/.attr() ..."等
     *
     * @return
     */
    public SelectType selectType() default SelectType.TEXT;

    /**
     * jquery 数据抽取参数，SelectType=ATTR/HAS_CLASS 时有效，如 ".attr("abs:src")"
     *
     * @return
     */
    public String selectVal() default "";

    /**
     * 时间格式化，日期类型数据有效
     *
     * @return
     */
    String datePattern() default "yyyy-MM-dd HH:mm:ss";

}
