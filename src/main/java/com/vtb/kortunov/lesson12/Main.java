package com.vtb.kortunov.lesson12;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            ReflectionRepository.connect();
            ReflectionRepository<Student> reflectionRepository = new ReflectionRepository<>(Student.class);
            Student student1 = new Student("Bob");
            Student student2 = new Student("Gena");
            reflectionRepository.save(student1);
            Student s = reflectionRepository.findById(1L);
            List<Student> students = reflectionRepository.findAll();
            reflectionRepository.deleteById(1L);
            reflectionRepository.deleteAll();
            System.out.println();
        } catch (ClassNotFoundException | SQLException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            ReflectionRepository.disconnect();
        }
    }
}
