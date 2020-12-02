package com.yunge.im.mode;

public class CallLogBean {
    public int beanType;
    public String name;
    public String number;
    public String date;
    public String duration;
    public int type;
    public String note;

    @Override
    public String toString() {
        return "CallLogBean{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", date='" + date + '\'' +
                ", duration=" + duration +
                ", type=" + type +
                ", note='" + note + '\'' +
                '}';
    }
}
