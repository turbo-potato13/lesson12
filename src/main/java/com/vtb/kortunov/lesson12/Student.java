package com.vtb.kortunov.lesson12;

import com.vtb.kortunov.lesson12.annotation.DbColumn;
import com.vtb.kortunov.lesson12.annotation.DbId;
import com.vtb.kortunov.lesson12.annotation.DbTable;

@DbTable(name = "mytable")
public class Student {

    @DbId
    public Long id;

    @DbColumn
    public String name;

    public  Student(){

    }

    public Student(String name) {
        this.name = name;
    }


}
