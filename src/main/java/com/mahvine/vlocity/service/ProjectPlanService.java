package com.mahvine.vlocity.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahvine.vlocity.model.ProjectPlan;
import com.mahvine.vlocity.model.Task;
import com.mahvine.vlocity.repository.ProjectPlanRepository;
import com.mahvine.vlocity.repository.TaskRepository;

@Service
public class ProjectPlanService {
    
    @Autowired
    private TaskRepository taskRepo;
    
    @Autowired
    private ProjectPlanRepository projectPlanRepo;
    
    public void addDependency(Task task, List<Long> dependencyIds){
        List<Task> allTasks = taskRepo.findByProjectPlan(task.getProjectPlan());
        allTasks = allTasks.stream().filter(projectTask -> dependencyIds.contains(projectTask.getId())).collect(Collectors.toList());
        
        
    }
    
    public ProjectPlan generateSchedule(ProjectPlan projectPlan, LocalDate startDate, int resourcePerson) {
        return projectPlan;
    }
}
