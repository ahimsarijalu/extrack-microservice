package com.ahimsarijalu.auth_service.utils;

import com.ahimsarijalu.auth_service.dto.FundDTO;
import com.ahimsarijalu.auth_service.dto.UserDTO;
import com.ahimsarijalu.auth_service.user.User;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class UserUtils {
    public static UserDTO mapUserToDTO(User user, List<FundDTO> funds) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setId(user.getId().toString());
        userDTO.setFunds(funds != null ? funds : List.of());
        return userDTO;
    }

    public static UserDTO mapUserToDTOWithoutFunds(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setId(user.getId().toString());
        return userDTO;
    }
}
