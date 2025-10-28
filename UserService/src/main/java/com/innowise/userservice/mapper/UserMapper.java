package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.request.UserRequestDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User entity);

    void updateEntityFromDto(UserRequestDto dto, @MappingTarget User entity);
}
