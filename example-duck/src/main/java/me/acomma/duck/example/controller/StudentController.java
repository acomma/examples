package me.acomma.duck.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.acomma.duck.example.common.StudentErrorCode;
import me.acomma.duck.example.common.dto.AddStudentDTO;
import me.acomma.duck.example.common.vo.StudentVO;
import me.acomma.duck.example.entity.Student;
import me.acomma.duck.example.service.StudentService;
import me.acomma.duck.util.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {
    private final StudentService studentService;

    @PostMapping("/add")
    public void add(@Validated @RequestBody AddStudentDTO dto) {
        Student student = new Student();
        BeanUtils.copyProperties(dto, student);

        studentService.add(student);
    }

    @GetMapping("/detail")
    public StudentVO detail(Integer id) {
        Student student = studentService.get(id);

        if (student == null) {
            throw new BusinessException(StudentErrorCode.STUDENT_NOT_EXIST);
        }

        StudentVO vo = new StudentVO();
        BeanUtils.copyProperties(student, vo);

        return vo;
    }

    @GetMapping("/detail/cache")
    public StudentVO detailFromCache(Integer id) {
        Student student = studentService.getFromCache(id);

        if (student == null) {
            throw new BusinessException(StudentErrorCode.STUDENT_NOT_EXIST);
        }

        StudentVO vo = new StudentVO();
        BeanUtils.copyProperties(student, vo);

        return vo;
    }
}
