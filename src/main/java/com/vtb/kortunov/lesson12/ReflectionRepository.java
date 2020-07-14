package com.vtb.kortunov.lesson12;

import com.vtb.kortunov.lesson12.annotation.DbColumn;
import com.vtb.kortunov.lesson12.annotation.DbId;
import com.vtb.kortunov.lesson12.annotation.DbTable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReflectionRepository<T> {
    private static Connection connection;
    private static Statement statement;
    private final Class<T> myClass;

    public ReflectionRepository(Class<T> myClass) {
        this.myClass = myClass;
    }

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");
        statement = connection.createStatement();
    }

    public void save(T object) throws SQLException, IllegalAccessException {
        if (!myClass.isAnnotationPresent(DbTable.class)) {
            throw new RuntimeException();
        }
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ");
        queryBuilder.append((myClass.getAnnotation(DbTable.class).name()));
        queryBuilder.append(" (");
        for (Field f : myClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(DbColumn.class)) {
                queryBuilder
                        .append(f.getName())
                        .append(", ");
            }
        }
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(") VALUES (");
        for (Field f : myClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(DbColumn.class)) {
                queryBuilder.append("'")
                        .append(f.get(object))
                        .append("');");
            }
        }
        System.out.println(queryBuilder);
        statement.executeUpdate(queryBuilder.toString());
    }

    public void deleteById(Long id) throws SQLException {
        if (!myClass.isAnnotationPresent(DbTable.class)) {
            throw new RuntimeException();
        }
        StringBuilder deleteBuilder = new StringBuilder();
        deleteBuilder.append("DELETE FROM ")
                .append((myClass.getAnnotation(DbTable.class).name()))
                .append(" WHERE id = ")
                .append(id);
        statement.executeUpdate(deleteBuilder.toString());
        System.out.println(deleteBuilder);

    }

    public void deleteAll() throws SQLException {
        if (!myClass.isAnnotationPresent(DbTable.class)) {
            throw new RuntimeException();
        }
        StringBuilder deleteAllBuilder = new StringBuilder();
        deleteAllBuilder.append("DELETE FROM ")
                .append((myClass.getAnnotation(DbTable.class).name()))
                .append(";");
        statement.executeUpdate(deleteAllBuilder.toString());
        System.out.println(deleteAllBuilder);
    }

    public T findById(Long id) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T object = myClass.getDeclaredConstructor().newInstance();
        if (!myClass.isAnnotationPresent(DbTable.class)) {
            throw new RuntimeException();
        }

        String findBuilder = "SELECT * FROM " +
                myClass.getAnnotation(DbTable.class).name() +
                " WHERE id = " +
                id;
        ResultSet resultSet = statement.executeQuery(findBuilder);
        for (Field f : object.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(DbId.class)) {
                f.set(object, resultSet.getLong(1));
            }
            if (f.isAnnotationPresent(DbColumn.class)) {
                f.set(object, resultSet.getString(2));
            }
        }
        return object;
    }

    public List<T> findAll() throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (!myClass.isAnnotationPresent(DbTable.class)) {
            throw new RuntimeException();
        }
        ArrayList<T> arrayList = new ArrayList<>();
        String findBuilder = "SELECT * FROM " +
                myClass.getAnnotation(DbTable.class).name() +
                ";";
        ResultSet resultSet = statement.executeQuery(findBuilder);
        while (resultSet.next()) {
            T object = myClass.getDeclaredConstructor().newInstance();
            for (Field f : object.getClass().getDeclaredFields()) {
                if (f.isAnnotationPresent(DbId.class)) {
                    f.set(object, resultSet.getLong(1));
                }
                if (f.isAnnotationPresent(DbColumn.class)) {
                    f.set(object, resultSet.getString(2));
                }
            }
            arrayList.add(object);
        }
        return arrayList;
    }


    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
