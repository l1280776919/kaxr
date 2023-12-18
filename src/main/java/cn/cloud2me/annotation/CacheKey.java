package cn.cloud2me.annotation;

import java.lang.annotation.*;

/**
 * @author liusong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
@Inherited
@Documented
public @interface CacheKey {
    String name() default "";
}
