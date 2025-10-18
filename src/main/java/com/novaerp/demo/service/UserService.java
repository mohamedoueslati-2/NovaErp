package com.novaerp.demo.service;

import com.novaerp.demo.model.dto.UserDto;
import com.novaerp.demo.model.entity.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    User findUserEntityById(Long id);
}