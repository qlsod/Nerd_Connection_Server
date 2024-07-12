package pallet_spring.model;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class MailRequestDto {

    @Email
    private String email;
}
