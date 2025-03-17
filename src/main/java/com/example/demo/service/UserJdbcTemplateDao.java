package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserJdbcTemplateDao {
    private JdbcTemplate jdbcTemplate;

    // JdbcTemplate이 SQLException을 내부에서 던지기와 처리를 다 해준다.
    public User findById(int userId) {
        String getUserQuery = "SELECT * FROM \"user\" WHERE id = ?";
        int getUserParams = userId;
        return this.jdbcTemplate.queryForObject(
            getUserQuery,
            (resultSet, rowNum) -> new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("age"),
                resultSet.getString("job"),
                resultSet.getString("specialty"),
                resultSet.getTimestamp("created_at")
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            ),
            getUserParams
        );
    }

    public List<User> findAll() {
        String getUsersQuery = "SELECT * FROM \"user\"";
        return jdbcTemplate.queryForStream(
            getUsersQuery,
            (resultSet, rowNum) -> new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("age"),
                resultSet.getString("job"),
                resultSet.getString("specialty"),
                resultSet.getTimestamp("created_at")
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            )
        ).toList();
    }

    public User save(String name, Integer age, String job, String specialty) {
        // (A) INSERT USER
        String createUserQuery = "INSERT INTO \"user\" (name, age, job, specialty, created_at) VALUES (?, ?, ?, ?, ?)";
        Object[] createUserParams = new Object[]{
            name, age, job, specialty, LocalDateTime.now()
        };
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        // (B) SELECT id
        String getLastIdQuery = "SELECT lastval()";
        int createdUserId = this.jdbcTemplate.queryForObject(getLastIdQuery, int.class);

        // (C) SELECT USER
        String getUserQuery = "SELECT * FROM \"user\" WHERE id = ?";
        int getUserParams = createdUserId;
        return this.jdbcTemplate.queryForObject(
            getUserQuery,
            (resultSet, rowNum)->new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("age"),
                resultSet.getString("job"),
                resultSet.getString("specialty"),
                resultSet.getTimestamp("created_at")
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            ),
            getUserParams
        );
    }
}
