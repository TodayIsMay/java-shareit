package shareit.user;

import shareit.user.dto.UserDto;
import shareit.user.model.User;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getName(), user.getEmail());
    }
}
