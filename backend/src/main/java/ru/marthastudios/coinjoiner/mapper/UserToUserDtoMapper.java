package ru.marthastudios.coinjoiner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.marthastudios.coinjoiner.dto.UserDto;
import ru.marthastudios.coinjoiner.model.User;

@Mapper(componentModel = "spring")
public interface UserToUserDtoMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "registeredAt", target = "registeredAt"),
    })
    UserDto userToUserDto(User user);
}
