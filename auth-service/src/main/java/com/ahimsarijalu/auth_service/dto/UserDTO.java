package com.ahimsarijalu.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private Integer age;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FundDTO> funds;
}
