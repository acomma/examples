package me.acomma.duck.example.common;

public enum Module implements me.acomma.duck.util.Module {
    STUDENT(11);

    private final Integer code;

    Module(Integer code) {
        this.code = code;
    }

    @Override
    public Integer code() {
        return code;
    }
}
