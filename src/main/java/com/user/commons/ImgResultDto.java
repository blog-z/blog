package com.user.commons;

public class ImgResultDto {

    private int  errno;//错误代码

    private String[] data;//存放数据
    //构造器
    //getter and setter


    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }
}
