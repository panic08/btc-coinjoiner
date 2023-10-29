package ru.marthastudios.coinjoiner.dto.vk;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VkUserDto {
    private Result[] response;
    private String errorMsg;
    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Result{
        private long id;
        private String photoMax;
        private boolean hasPhoto;
        private String firstName;
        private String lastName;
    }
}