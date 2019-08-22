package com.mahvine.vlocity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahvine.vlocity.model.ProjectPlan;
import com.mahvine.vlocity.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{
    List<Task> findByProjectPlan(ProjectPlan project);
    
}
