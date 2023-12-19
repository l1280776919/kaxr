package cn.cloud2me.annotation;

import java.lang.annotation.*;

/**
 * 指定缓存使用哪个key 在方法返回的实体类或者字段上使用 将他们组合起来当做缓存key
 * @author liusong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
@Inherited
@Documented
public @interface CacheKey {
    String name() default "";
}
