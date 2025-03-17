package com.example.demo.service;

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
}
