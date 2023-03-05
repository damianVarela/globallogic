package com.evaluation.globallogic.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
public class AccountDTO {

    private String name;

    @Email
    private String email;

    @Pattern(regexp = "^(?=[^A-Z]*[A-Z][^A-Z]*$)(?=\\D*\\d\\D*\\d\\D*$)[a-zA-Z0-9]{8,12}$",
            message = "password must contain exactly two digits and exactly one upper case character in combination of lower case characters. its length must be between [8-12] characters.")
    private String password;

    private Set<PhoneDTO> phones;
}
