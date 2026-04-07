package com.waveneo.neowaves;

import com.waveneo.neowaves.models.User;
import com.waveneo.neowaves.repositories.UserCrudRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserCrudRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserCrudRepository userCrudRepository;

    @Test
    void findByEmailReturnsCorrectUser() {
        User user = new User(null, "John", "pupik@gmail.com");
        testEntityManager.persistAndFlush(user);
        Optional<User> userOptional = userCrudRepository.findByEmail("pupik@gmail.com");

        assertTrue(userOptional.isPresent(), "Usser gay");
        assertEquals("John", userOptional.get().getUsername());
        assertEquals("pupik@gmail.com", userOptional.get().getEmail());
    }
}
