package pallet_spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor
public class NicknamePatchDto {

    @NotBlank
    private String name;
}
