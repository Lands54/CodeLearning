//zgx
package org.example.ServerBlock;

import java.sql.*;

import static java.lang.Thread.sleep;

public class SQLite {
    Connection db;
    Statement stmt;
    void createDB(String database) throws SQLException {
        db = DriverManager.getConnection("jdbc:sqlite:" + database + ".db");
    }

    void setForeignKey(boolean mod) throws SQLException {
        write("PRAGMA foreign_keys = " + (mod ? "ON;" : "OFF;"));
    }

    void setStmt() throws SQLException {
        stmt = db.createStatement();
    }

    void createTable(String table, String[] Value) throws SQLException {
        StringBuffer command = new StringBuffer("CREATE TABLE " + table);
        command.append('(');
        for (String i : Value) {
            command.append(i);
        }
        command.append(");");
        write(command.toString());
    }

    void insertDate(String table, String attribute, String[] Value) throws SQLException {
        StringBuffer command = new StringBuffer("INSERT INTO " + table + "(" + attribute + ") " + "VALUES (");
        for (String value : Value) {
            command.append(value);
            command.append("),");
        }
        command.deleteCharAt(command.length() - 1);
        command.append(";");
        write(command.toString());
    }

    void write(String data) throws SQLException {
        synchronized (this){
            setStmt();
            stmt.executeUpdate(data);
        }
    }

    ResultSet get(String date) throws SQLException {
        synchronized (this){
            setStmt();
            return stmt.executeQuery(date);
        }
    }

}
