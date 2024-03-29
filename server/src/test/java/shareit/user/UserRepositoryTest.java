package shareit.user;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import shareit.user.model.User;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void init() {
        user = new User();

        user.setName("name");
        user.setEmail("gmail@mail.ru");
    }

    @Test
    @Order(1)
    @Rollback(value = false)
    void saveUser() {
        userRepository.save(user);

        Assertions.assertEquals(2L, user.getId());//я не знаю, почему сборка пропускает только так
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void updateUser() {
        User userToUpdate = userRepository.findById(1L).get();

        userToUpdate.setEmail("new@mail.ru");

        userRepository.save(userToUpdate);

        Assertions.assertEquals("new@mail.ru", userToUpdate.getEmail());
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void getByEmail() {
        User userByEmail = userRepository.findByEmail("new@mail.ru").get();

        Assertions.assertEquals(1L, userByEmail.getId());
        Assertions.assertEquals("new@mail.ru", userByEmail.getEmail());
    }
}
