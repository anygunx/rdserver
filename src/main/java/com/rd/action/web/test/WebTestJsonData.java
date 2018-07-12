package com.rd.action.web.test;

import com.alibaba.fastjson.JSON;
import com.rd.util.HttpUtil;

public class WebTestJsonData {

    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WebTestJsonData [id=" + id + ", name=" + name + "]";
    }

    public static void main(String[] args) {
        WebTestJsonData data = new WebTestJsonData();
        data.setId(1);
        data.setName("zzq");
        String json = JSON.toJSON(data).toString();
        System.out.println(json);
        String result = HttpUtil.sendHttpGet("http://127.0.0.1:3001/testJson", ".do",
                json);
        System.out.println(result);
    }
}
