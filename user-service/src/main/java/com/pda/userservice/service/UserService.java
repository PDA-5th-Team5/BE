package com.pda.userservice.service;

import com.pda.userservice.dto.request.JoinDTO;

public interface UserService {

    boolean join(JoinDTO joinDTO);
}
