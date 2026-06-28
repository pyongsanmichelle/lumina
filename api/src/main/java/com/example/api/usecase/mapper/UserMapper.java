package com.example.api.usecase.mapper;

import com.example.api.domain.User;
import com.example.api.presentation.response.UserResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * UserエンティティとUserResponseのマッピングを行うMapStructマッパー
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * UserエンティティからUserResponseへのマッピング
     */
    public abstract UserResponse toResponse(User user);

    /**
     * UserエンティティのリストからUserResponseのリストへのマッピング
     */
    public abstract List<UserResponse> toResponseList(List<User> users);

    /**
     * マッピング後の処理：OffsetDateTimeをStringに変換
     */
    @AfterMapping
    protected void formatDates(User user, @MappingTarget UserResponse response) {
        if (user.getCreatedAt() != null) {
            response.setCreatedAt(user.getCreatedAt().format(FORMATTER));
        }
        if (user.getUpdatedAt() != null) {
            response.setUpdatedAt(user.getUpdatedAt().format(FORMATTER));
        }
    }
}