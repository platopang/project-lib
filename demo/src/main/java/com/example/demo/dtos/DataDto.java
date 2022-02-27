package com.example.demo.dtos;

public class DataDto {

    private Object data;

    public DataDto(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
