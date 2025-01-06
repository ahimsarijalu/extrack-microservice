package com.ahimsarijalu.auth_service.auth;

import com.ahimsarijalu.auth_service.dto.LoginDTO;
import com.ahimsarijalu.auth_service.dto.LoginResponse;
import com.ahimsarijalu.auth_service.dto.RegisterDTO;
import com.ahimsarijalu.auth_service.user.User;
import com.ahimsarijalu.auth_service.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static com.ahimsarijalu.auth_service.utils.MapperUtil.mapToApiResponse;
import static com.ahimsarijalu.auth_service.utils.UserUtils.mapUserToDTO;
import static com.ahimsarijalu.auth_service.utils.UserUtils.mapUserToDTOWithoutFunds;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenProvider tokenProvider;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterDTO registerDTO) throws Exception {
        authService.register(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToApiResponse(HttpStatus.CREATED.value(), true, "User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginDTO loginDTO) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
        var authUser = authenticationManager.authenticate(usernamePassword);
        var user = (User) authUser.getPrincipal();
        var token = tokenProvider.generateAccessToken(user);

        var jwt = new LoginResponse();
        jwt.setToken(token);
        jwt.setUser(mapUserToDTOWithoutFunds(user));
        return ResponseEntity.ok(mapToApiResponse(HttpStatus.OK.value(), true, "User logged in successfully", jwt));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        try {
            String userEmail = tokenProvider.validateToken(token);
            return ResponseEntity.ok("valid");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid");
        }
    }
}
