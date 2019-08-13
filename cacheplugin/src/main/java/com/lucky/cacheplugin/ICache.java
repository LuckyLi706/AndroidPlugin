package com.lucky.cacheplugin;

/**
 * 作者：jacky on 2019/8/12 20:15
 * 邮箱：jackyli706@gmail.com
 */
public interface ICache {

    void put();

    Object get();

    void remove();
}
