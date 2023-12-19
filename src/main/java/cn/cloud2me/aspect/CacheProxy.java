package cn.cloud2me.aspect;

import cn.cloud2me.annotation.Cache;
import cn.cloud2me.annotation.CacheKey;
import cn.cloud2me.constant.CacheType;
import cn.cloud2me.service.ICacheOperate;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author liusong
 * put cn.cloud2me.aspect in scanBasePackages
 */
@Aspect
@Component
@Slf4j
public class CacheProxy {

    @Autowired
    private ICacheOperate iCacheOperate;

    @Pointcut(value = "@annotation(cn.cloud2me.annotation.Cache)")
    public void pointcut() {

    }

    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        try {
            // 获取目标方法
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            // 获取方法上的注解
            Cache cache = methodSignature.getMethod().getAnnotation(Cache.class);
            String key = cache.key();
            CacheType type = cache.type();
            String suffix = "";
            // 如果目标方法里没有key 则从返回数据的对象里找，如果还找不到，就不缓存
            if (!StringUtils.hasText(key)) {
                Class<?> clazz = result.getClass();
                // 获取类上的注解
                if (clazz.isAnnotationPresent(CacheKey.class)) {
                    CacheKey classKey = clazz.getAnnotation(CacheKey.class);
                    if (!StringUtils.hasText(classKey.name())){
                        key = clazz.getName();
                    } else {
                        key = classKey.name();
                    }
                }
            }
            // 获取字段上的注解
            Object fieldKey = getFirstAnnotatedFieldValue(result, CacheKey.class);
            if (fieldKey != null) {
                suffix =String.valueOf(fieldKey);
            }
            log.info("obj name : [{}] ,final key : [{}]", result.getClass().getSimpleName(), key);
            if (type == CacheType.INSERT || type == CacheType.UPDATE){
                iCacheOperate.set(key + suffix,result,cache.timeout());
            } else if (type == CacheType.DELETE) {
                iCacheOperate.delete(key);
            }
        } catch (Exception e) {
            log.error("cache error",e);
        }

        return result;
    }

    public static Object getFirstAnnotatedFieldValue(Object object, Class<? extends Annotation> annotationClass) {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                try {
                    // 设置可访问，以便访问私有字段
                    field.setAccessible(true);
                    // 返回第一个带有目标注解的字段的值
                    return field.get(object);
                } catch (IllegalAccessException e) {
                    log.error("get field value error",e);
                }
            }
        }

        return null; // 如果没有找到带有目标注解的字段，返回null或者适当的默认值
    }

}
