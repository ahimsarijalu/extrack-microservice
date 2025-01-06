package com.ahimsarijalu.auth_service.user;

import com.ahimsarijalu.auth_service.client.FundClient;
import com.ahimsarijalu.auth_service.dto.FundDTO;
import com.ahimsarijalu.auth_service.dto.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.ahimsarijalu.auth_service.utils.MapperUtil.mapDTOToEntity;
import static com.ahimsarijalu.auth_service.utils.UserUtils.mapUserToDTO;
import static com.ahimsarijalu.auth_service.utils.UserUtils.mapUserToDTOWithoutFunds;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FundClient fundClient;

    public UserDTO getUserById(String id) {
        List<FundDTO> funds = fundClient.getFundsByUserId(id).getData();
        return userRepository.findById(UUID.fromString(id))
                .map(user -> mapUserToDTO(user, funds))
                .orElseThrow(() -> new EntityNotFoundException("User with this id: " + id + " is not found"));
    }

    public UserDTO saveUser(UserDTO userDTO) {
        User user = mapDTOToEntity(userDTO, User.class);
        user = userRepository.save(user);

        return mapUserToDTOWithoutFunds(user);  // Return the saved user DTO without funds
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("User with this id: " + id + " is not found"));

        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getAge() != null) {
            user.setAge(userDTO.getAge());
        }

        user = userRepository.save(user);

        return mapUserToDTOWithoutFunds(user);
    }

    public void deleteUser(String id) {
        if (userRepository.findById(UUID.fromString(id)).isEmpty()) {
            throw new EntityNotFoundException("User with this id: " + id + " is not found");
        }
        userRepository.deleteById(UUID.fromString(id));
    }
}
