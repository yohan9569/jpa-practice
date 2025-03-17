package com.example.demo.service;

import java.sql.*;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Repository
public class UserJdbcApiDao {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public User findById(int userId) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM \"user\" WHERE id = " + userId);
            if (resultSet.next()) {
                return new User(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getInt("age"),
                    resultSet.getString("job"),
                    resultSet.getString("specialty"),
                    resultSet.getTimestamp("created_at")
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                );
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저 정보가 존재하지 않습니다 - id : " + userId);
        } catch (SQLException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "자원에 대한 접근에 문제가 있습니다.");
        } finally {
            // 자원 반납
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();

        }
    }

}
