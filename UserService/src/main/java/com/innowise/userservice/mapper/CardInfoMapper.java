package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.request.CardInfoRequestDto;
import com.innowise.userservice.dto.response.CardInfoResponseDto;
import com.innowise.userservice.model.CardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {
    @Mapping(source = "userId", target = "user.id")
    CardInfo toEntity(CardInfoRequestDto dto);

    @Mapping(source = "user.id", target = "userId")
    CardInfoResponseDto toDto(CardInfo entity);

    void updateEntityFromDto(CardInfoRequestDto dto, @MappingTarget CardInfo entity);
}
