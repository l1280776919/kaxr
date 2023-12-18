package cn.cloud2me.service;

/**
 * @author liusong
 */
public interface ICacheOperate {
    void set(String key,Object value,long timeout);
    void delete(String key);
}
