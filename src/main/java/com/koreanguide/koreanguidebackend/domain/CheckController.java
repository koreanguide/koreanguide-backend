package com.koreanguide.koreanguidebackend.domain;

import com.koreanguide.koreanguidebackend.domain.auth.data.dao.UserDao;
import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.auth.data.repository.SignLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/check")
@RequiredArgsConstructor
public class CheckController {
    private final SignLogRepository signLogRepository;
    private final UserDao userDao;

    @GetMapping("/")
    public String checkManagerSigned() {
        User user = userDao.getUserEntity(3L);
        if(signLogRepository.getAllByUser(user).isEmpty()) {
            return "서류 심사를 위한 로그인 전(대략 오후 6시 ~ 익일 오전 9시 사이 수정 가능)";
        } else {
            return "서류 심사를 위한 로그인 완료(서류 심사에 추후 수정이 포함되지 않음)";
        }
    }
}
