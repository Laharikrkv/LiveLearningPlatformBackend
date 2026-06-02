package com.example.live_learning.users;

import org.springframework.stereotype.Service;

import com.example.live_learning.common.exceptions.ResourceNotFoundException;
import com.example.live_learning.users.entity.User;
import com.example.live_learning.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        return new UserDto(user.getId(), user.getTimezone());
    }
}
