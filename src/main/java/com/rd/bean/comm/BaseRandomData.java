package com.rd.bean.comm;

/**
 * Created by XingYun on 2017/11/6.
 */
public abstract class BaseRandomData<T> implements IRandomData {
    protected T data;
    protected int weight;

    public BaseRandomData(T data, int weight) {
        this.data = data;
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    public T getData() {
        return data;
    }

}
