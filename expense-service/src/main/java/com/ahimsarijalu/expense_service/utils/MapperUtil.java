package com.ahimsarijalu.expense_service.utils;

import org.springframework.beans.BeanUtils;

public class MapperUtil {
    private MapperUtil() {
    }

    public static <T, U> T mapDTOToEntity(U u, Class<T> targetType) {
        try {
            T t = targetType.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(u, t);
            return t;
        } catch (Exception e) {
            throw new RuntimeException("Error while mapping DTO to Entity", e);
        }
    }

    public static <T> ApiResponse<T> mapToApiResponse(Integer responseCode, Boolean success, String message, T data) {
        return new ApiResponse<>(
                responseCode,
                success,
                message,
                data
        );
    }
}
