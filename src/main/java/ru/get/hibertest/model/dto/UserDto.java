package ru.get.hibertest.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.get.hibertest.validation.AddGroup;

@Data
@Builder
public class UserDto {
    protected Long id;

    @NotNull(message = "Username is mandatory", groups = {AddGroup.class})
    @NotBlank(message = "Firstname should not be empty", groups = {AddGroup.class})
    private String username;
    @NotNull(message = "Firstname is mandatory")
    @NotBlank(message = "Firstname should not be empty")
    private String firstname;
    @Column
    @NotNull(message = "Lastname is mandatory")
    @NotBlank(message = "Lastname should not be empty")
    private String lastname;
    @NotNull(message = "Email is mandatory", groups = {AddGroup.class})
    @NotBlank(message = "Email should not be empty", groups = {AddGroup.class})
    @Email(message = "Email is not correct", groups = {AddGroup.class})
    private String email;
}
