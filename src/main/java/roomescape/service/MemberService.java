package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.User;
import roomescape.dto.auth.RegisterRequest;
import roomescape.dto.auth.RegisterResponse;
import roomescape.exception.DuplicateEmailException;
import roomescape.repository.UserDao;

@Service
public class MemberService {

    private final UserDao userDao;

    public MemberService(UserDao userDao) {
        this.userDao = userDao;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userDao.existsByEmail(request.email())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다: " + request.email());
        }

        User newUser = new User(null, request.name(), request.email(), request.password());
        User saved = userDao.save(newUser);
        return RegisterResponse.from(saved);
    }
}
