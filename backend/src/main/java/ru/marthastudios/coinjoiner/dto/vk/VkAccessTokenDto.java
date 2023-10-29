package ru.marthastudios.coinjoiner.dto.vk;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VkAccessTokenDto {
    private String accessToken;
    private long expiresIn;
    private long userId;
}
