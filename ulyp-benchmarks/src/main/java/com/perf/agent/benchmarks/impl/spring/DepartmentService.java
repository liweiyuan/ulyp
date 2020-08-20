package com.perf.agent.benchmarks.impl.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    public DepartmentJpaRepository repository;

    public void save(Department department) {
        repository.save(department);
    }

    public void shufflePeople() {
//        List<Department> departments = repository.findAll();
    }

    public int countPeople() {
        int count = 0;
        for (Department department : repository.findAll()) {
            count += department.getPeople().size();
        }
        return count;
    }

    public void removeAll() {
        repository.deleteAll();
    }
}
