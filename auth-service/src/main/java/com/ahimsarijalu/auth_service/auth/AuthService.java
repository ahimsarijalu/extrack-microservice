package com.ahimsarijalu.auth_service.auth;


import com.ahimsarijalu.auth_service.dto.RegisterDTO;
import com.ahimsarijalu.auth_service.user.User;
import com.ahimsarijalu.auth_service.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.ahimsarijalu.auth_service.utils.MapperUtil.mapDTOToEntity;


@Service
public class AuthService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    public void register(RegisterDTO registerDTO) throws Exception {
        if (userRepository.findByEmail(registerDTO.getEmail()) != null) {
            throw new InvalidJwtException("Email already exists");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.getPassword());
        User newUser = mapDTOToEntity(registerDTO, User.class);
        newUser.setPassword(encryptedPassword);
        userRepository.save(newUser);
    }
}
