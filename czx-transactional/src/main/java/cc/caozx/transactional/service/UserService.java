package cc.caozx.transactional.service;

import cc.caozx.transactional.entity.User;
import cc.caozx.transactional.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService self;

    public int createUserWrong1(String name) {
        try {
            this.createUserPrivate(new User(name));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return userRepository.findByName(name).size();
    }

    public int createUserWrong2(String name) {
        try {
            this.createUserPublic(new User(name));
        } catch (Exception ex) {
            log.error("create user failed because {}", ex.getMessage());
        }
        return userRepository.findByName(name).size();
    }

    @Transactional
    public void createUserPublic(User user) {
        userRepository.save(user);
        if (user.getName().contains("test"))
            throw new RuntimeException("invalid username!");
    }

    @Transactional
    private void createUserPrivate(User user) {
        userRepository.save(user);
        if (user.getName().contains("test"))
            throw new RuntimeException("invalid username!");
    }

    // 根据用户名查询用户数
    public int getUserCount(String name) {
        return userRepository.findByName(name).size();
    }
}
