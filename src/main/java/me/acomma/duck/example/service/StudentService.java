package me.acomma.duck.example.service;

import lombok.RequiredArgsConstructor;
import me.acomma.duck.boot.cache.CacheExtension;
import me.acomma.duck.boot.jdbc.DataSourceSelector;
import me.acomma.duck.boot.jdbc.DataSourceType;
import me.acomma.duck.boot.lock.Lockable;
import me.acomma.duck.example.entity.Student;
import me.acomma.duck.example.mapper.StudentMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentMapper studentMapper;

    @Lockable(key = "'lock:' + #student.name")
    public void add(Student student) {
        studentMapper.insert(student);
    }

    @DataSourceSelector(DataSourceType.REPLICA)
    public Student get(Integer id) {
        return studentMapper.select(id);
    }

    @Cacheable(cacheNames = {"student"}, key = "#id")
    @CacheExtension(ttl = 60)
    public Student getFromCache(Integer id) {
        return studentMapper.select(id);
    }
}
