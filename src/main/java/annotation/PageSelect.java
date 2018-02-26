package annotation;

import java.lang.annotation.*;

/**
 * 页面级别注解
 * 注解到类上,表示只爬取该范围内的数据
 * Created by wangyang on 2018/2/24.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PageSelect {

    /**
     * <p>
     * CSS选择器, 如 "#body"
     *
     * @return
     */
    public String cssQuery() default "";

}
