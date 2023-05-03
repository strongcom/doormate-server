package com.strongcom.doormate.service;

import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.UserDto;

public interface UserService {

    public User createUser(UserDto userDto);

}
