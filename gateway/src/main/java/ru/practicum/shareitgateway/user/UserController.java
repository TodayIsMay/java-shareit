package ru.practicum.shareitgateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public Object getAll() {
        return userClient.getAll();
    }

    @PostMapping
    public Object createUser(@RequestBody @Valid UserDto request) {
        log.info("Creating user {}", request.toString());

        return userClient.createUser(request);
    }

    @GetMapping("/{userId}")
    public Object getById(@PathVariable("userId") @Positive Long userId) {
        return userClient.getById(userId);
    }

    @PatchMapping("/{userId}")
    public Object updateUser(@PathVariable("userId") @Positive Long userId,
                             @RequestBody @Valid User user) {
        return userClient.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userClient.deleteUser(userId);
    }
}