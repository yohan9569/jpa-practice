package com.example.demo.service;

import com.example.demo.repository.entity.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MessageJdbcApiDao {
    private final DataSource dataSource;

    public List<Message> findByUserId(int userId) throws SQLException {
        Connection connection = null;           // 1
        PreparedStatement statement = null;     // 2
        ResultSet resultSet = null;             // 3

        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement("SELECT * FROM \"message\" WHERE user_id = ?");
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            List<Message> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(
                    new Message(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("message"),
                        resultSet.getTimestamp("created_at")
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                    )
                );
            }
            return results;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "자원에 대한 접근에 문제가 있습니다.");
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }

    public List<Message> save(Integer userId, String message) throws SQLException {
        Connection connection = null;           // 1
        PreparedStatement statement = null;     // 2
        ResultSet resultSet = null;             // 3

        try {
            connection = dataSource.getConnection();
            // INSERT MESSAGE
            statement = connection.prepareStatement( "INSERT INTO \"message\" (user_id, message, created_at) VALUES (?, ?, ?)");
            statement.setInt(1, userId);
            statement.setString(2, message);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            int executedNumberOfQuery = statement.executeUpdate();

            // SELECT MESSAGE
            statement = connection.prepareStatement("SELECT * FROM \"message\" WHERE user_id = ?");
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            List<Message> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(
                    new Message(
                        resultSet.getInt("id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("message"),
                        resultSet.getTimestamp("created_at")
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                    )
                );
            }
            return results;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                String.format("메세지가 저장되지 않았거나 자원에 대한 접근에 문제가 있습니다. - userId : %s, message : %s",
                    userId,
                    message
                )
            );
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }
}
