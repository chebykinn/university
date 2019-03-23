package org.chebykin.webservices.lab1;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sun.util.logging.LoggingSupport.log;

public class Person {
    private String name;
    private String surname;
    private String job;
    private String city;
    private int age;
    private byte[] avatar;
    public Person() {
    }
    public Person(String name, String surname, String job, String city, int age) {
        this.name = name;
        this.surname = surname;
        this.job = job;
        this.city = city;
        this.age = age;
    }
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public int getAge() {
        return age;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public void setAvatar(byte[] avatar) { this.avatar = avatar; }
    @Override
    public String toString() {
        return "Person{" + "name=" + name + ", surname=" + surname + ", job=" + job + ", city=" + city + ", age=" + age + '}';
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public byte[] getAvatar() { return avatar; }

    public String getFieldValue(String field) throws NoSuchFieldException, IllegalAccessException {
        Field f = this.getClass().getDeclaredField(field);
        Object val = f.get(this);
        return val.toString();
    }
}
