package cn.cloud2me.annotation;

import cn.cloud2me.constant.CacheType;

import java.lang.annotation.*;

/**
 * @author liusong
 * 缓存数据标识类 作用于spring代理对象方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface Cache {
    /**
     * 缓存key，可从CacheKey动态取
     */
    String key() default "";

    /**
     * 超时时间 默认无限
     */
    long timeout() default -1;
    /**
     *
     */
    CacheType type() default CacheType.INSERT;

}
