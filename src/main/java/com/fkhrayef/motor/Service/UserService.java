package com.fkhrayef.motor.Service;

import com.fkhrayef.motor.Api.ApiException;
import com.fkhrayef.motor.DTOin.UserDTO;
import com.fkhrayef.motor.Model.User;
import com.fkhrayef.motor.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // TODO: switch it to register and use registerDTO
    public void addUser(UserDTO userDTO) {
        User user = new User();
        // set DTO values
        user.setPhone(userDTO.getPhone());
        user.setName(user.getName());
        user.setEmail(user.getEmail());
        user.setPassword(user.getPassword());

        // set default values
        user.setRole("USER");

        userRepository.save(user);
    }

    // TODO: add endpoints to enter license

    public void updateUser(Integer id, UserDTO userDTO) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new ApiException("User not found");
        }

        user.setPhone(userDTO.getPhone());
        user.setName(user.getName());
        user.setEmail(user.getEmail());
        user.setPassword(user.getPassword());

        userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new ApiException("User not found");
        }

        userRepository.delete(user);
    }
}
