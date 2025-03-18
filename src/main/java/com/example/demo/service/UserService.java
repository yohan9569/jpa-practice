package com.example.demo.service;

import com.example.demo.controller.dto.UserResponseDto;
import com.example.demo.repository.entity.Message;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.processing.SQL;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserJdbcApiDao userJdbcRepository;
    private final UserJdbcTemplateDao userJdbcTemplateRepository;
    private final MessageJdbcApiDao messageJdbcRepository;

    // JdbcTemplate이 SQLException을 내부에서 던지기와 처리를 다 해준다.
    public UserResponseDto findById(Integer id) {
        User user = userJdbcTemplateRepository.findById(id);
        return UserResponseDto.from(user);
    }

    public List<UserResponseDto> findAll() {
        return userJdbcTemplateRepository.findAll()
            .stream()
            .map(UserResponseDto::from)
            .toList();
    }

    public UserResponseDto save(String name, Integer age, String job, String specialty) {
       try {
            User user = userJdbcRepository.save(name, age, job, specialty);
            List<Message> messages = messageJdbcRepository.save(user.getId(), user.getName() + "님 가입을 환영합니다.");
            UserResponseDto result = UserResponseDto.from(user);
            result.setMessages(messages);
            return result;
       } catch (SQLException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "자원 반납 시 문제가 있습니다.");
       }
    }

    public UserResponseDto update(Integer id, String name, Integer age, String job, String specialty) {
        User user = userJdbcTemplateRepository.update(id, name, age, job, specialty);
        return UserResponseDto.from(user);
    }

    public void delete(Integer id) {
        userJdbcTemplateRepository.delete(id);
    }
}
