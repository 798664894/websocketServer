package com.winning.demo.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * description:{获取泛型类的type}
 * date:2017-08-21 13:12
 * modify:{modify}
 */
public class ParameterizedTypeImpl implements ParameterizedType{
    private final Class raw;
    private final Type[] args;
    public ParameterizedTypeImpl(Class raw, Type[] args) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
    }
    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }
    @Override
    public Type getRawType() {
        return raw;
    }
    @Override
    public Type getOwnerType() {return null;}
}
