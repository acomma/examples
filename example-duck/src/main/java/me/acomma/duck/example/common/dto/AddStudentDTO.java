package me.acomma.duck.example.common.dto;

import lombok.Getter;
import lombok.Setter;
import me.acomma.duck.example.common.Gender;
import me.acomma.duck.util.validation.FixedValue;

import java.util.Date;

@Getter
@Setter
public class AddStudentDTO {
    private String name;

    private Integer age;

    @FixedValue(strings = {Gender.MALE, Gender.FEMALE})
    private String gender;

    private Date birthday;
}
