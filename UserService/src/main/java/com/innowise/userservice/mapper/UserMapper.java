package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.request.UserRequestDto;
import com.innowise.userservice.dto.response.UserResponseDto;
import com.innowise.userservice.dto.response.UserWithCardsResponseDto;
import com.innowise.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "email", ignore = true)
    User toEntity(UserRequestDto dto);

    UserResponseDto toDto(User entity);

    @Mapping(target = "email", ignore = true)
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget User entity);

    UserWithCardsResponseDto toDtoWithCards(User entity);
}
