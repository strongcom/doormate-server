package com.strongcom.doormate.service;

import com.strongcom.doormate.domain.User;
import com.strongcom.doormate.dto.UserDto;
import com.strongcom.doormate.kakao.dto.AddInfoRequest;

public interface UserService {

    public User createUser(UserDto userDto);

    public void addUserInfo(AddInfoRequest addInfoRequest, Long userId);


}
