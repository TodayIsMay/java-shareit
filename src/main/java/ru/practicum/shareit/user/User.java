package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;

/**
 * // TODO .
 */
@AllArgsConstructor
@Data
public class User {
    private long id;
    private String name;
    private String email;
}
