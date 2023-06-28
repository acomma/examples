package me.acomma.duck.example.common;

import me.acomma.duck.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gender implements Constant<String> {
    public static final String MALE = "M";
    public static final String FEMALE = "F";

    public static final List<String> LIST = new ArrayList<>();
    public static final Map<String, String> MAP = new HashMap<>();

    static {
        LIST.add(MALE);
        LIST.add(FEMALE);

        MAP.put(MALE, "男");
        MAP.put(FEMALE, "女");
    }

    @Override
    public List<String> list() {
        return LIST;
    }

    @Override
    public Map<String, String> map() {
        return MAP;
    }

    @Override
    public boolean contains(String c) {
        return LIST.contains(c);
    }

    @Override
    public String description(String c) {
        return MAP.get(c);
    }
}
