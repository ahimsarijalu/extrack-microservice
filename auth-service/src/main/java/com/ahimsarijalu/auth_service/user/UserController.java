package com.ahimsarijalu.auth_service.user;

import com.ahimsarijalu.auth_service.dto.UserDTO;
import com.ahimsarijalu.auth_service.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ahimsarijalu.auth_service.utils.MapperUtil.mapToApiResponse;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(mapToApiResponse(HttpStatus.OK.value(), true, "Users fetched successfully", user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable String id, @RequestBody UserDTO userDTO) {
        userService.updateUser(id, userDTO);
        UserDTO updatedUser = userService.getUserById(id);

        return ResponseEntity.ok(mapToApiResponse(HttpStatus.OK.value(), true, "User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(mapToApiResponse(HttpStatus.OK.value(), true, "User deleted successfully", null));
    }

}
