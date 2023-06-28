package me.acomma.duck.example.common;

import me.acomma.duck.util.Module;
import me.acomma.duck.util.code.BusinessErrorCode;

public enum StudentErrorCode implements BusinessErrorCode {
    STUDENT_NOT_EXIST(1, "学生不存在");

    private final Integer number;

    private final String message;

    StudentErrorCode(Integer number, String message) {
        this.number = number;
        this.message = message;
    }

    @Override
    public Integer number() {
        return number;
    }

    @Override
    public Module module() {
        return me.acomma.duck.example.common.Module.STUDENT;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public Integer value() {
        return code();
    }
}
