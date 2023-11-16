package me.acomma.duck.example.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentVO {
    private Integer id;

    private String name;

    private Integer age;

    private String gender;

    private Date birthday;
}
