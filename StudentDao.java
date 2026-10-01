package com.studenthub.dao;

import com.studenthub.config.DatabaseConfig;
import com.studenthub.model.Student;
import java.sql.*;
import java.util.*;

public class StudentDao {
    private Connection connection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.url(), DatabaseConfig.user(), DatabaseConfig.password());
    }

    public List<Student> findAll(String search) throws SQLException {
        String sql = "SELECT id,name,email,course,year_level,phone FROM students " +
                     "WHERE name LIKE ? OR email LIKE ? OR course LIKE ? ORDER BY id DESC";
        String key = "%" + (search == null ? "" : search.trim()) + "%";
        List<Student> result = new ArrayList<>();

        try (Connection con=connection(); PreparedStatement ps=con.prepareStatement(sql)) {
            ps.setString(1,key); ps.setString(2,key); ps.setString(3,key);
            try (ResultSet rs=ps.executeQuery()) {
                while(rs.next()) result.add(map(rs));
            }
        }
        return result;
    }

    public Student findById(int id) throws SQLException {
        String sql="SELECT id,name,email,course,year_level,phone FROM students WHERE id=?";
        try(Connection con=connection(); PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1,id);
            try(ResultSet rs=ps.executeQuery()){
                return rs.next()?map(rs):null;
            }
        }
    }

    public int create(Student s) throws SQLException {
        String sql="INSERT INTO students(name,email,course,year_level,phone) VALUES(?,?,?,?,?)";
        try(Connection con=connection();
            PreparedStatement ps=con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            set(ps,s); ps.executeUpdate();
            try(ResultSet keys=ps.getGeneratedKeys()){
                if(keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Could not create student");
    }

    public boolean update(int id, Student s) throws SQLException {
        String sql="UPDATE students SET name=?,email=?,course=?,year_level=?,phone=? WHERE id=?";
        try(Connection con=connection(); PreparedStatement ps=con.prepareStatement(sql)){
            set(ps,s); ps.setInt(6,id); return ps.executeUpdate()>0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try(Connection con=connection();
            PreparedStatement ps=con.prepareStatement("DELETE FROM students WHERE id=?")){
            ps.setInt(1,id); return ps.executeUpdate()>0;
        }
    }

    private void set(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1,s.getName()); ps.setString(2,s.getEmail());
        ps.setString(3,s.getCourse()); ps.setInt(4,s.getYearLevel());
        ps.setString(5,s.getPhone());
    }

    private Student map(ResultSet rs) throws SQLException {
        return new Student(rs.getInt("id"),rs.getString("name"),rs.getString("email"),
            rs.getString("course"),rs.getInt("year_level"),rs.getString("phone"));
    }
}
