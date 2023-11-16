package me.acomma.duck.example.mapper;

import me.acomma.duck.example.entity.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper {
    @Insert("INSERT INTO student(name, age, gender, birthday) VALUES(#{name}, #{age}, #{gender}, #{birthday})")
    void insert(Student student);

    @Select("SELECT * FROM student where id = #{id}")
    Student select(@Param("id") Integer id);
}
