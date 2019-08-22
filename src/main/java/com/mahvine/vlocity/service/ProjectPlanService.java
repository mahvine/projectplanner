package com.mahvine.vlocity.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    public void setDependency(Task task, List<Long> dependencyIds){
        List<Task> allTasks = taskRepo.findByProjectPlan(task.getProjectPlan());
        allTasks = allTasks.stream().filter(projectTask -> dependencyIds.contains(projectTask.getId())).collect(Collectors.toList());
        List<Task> allDependencies = new ArrayList<Task>();
        allTasks.stream().forEach( dependency -> {
        	allDependencies.add(dependency);
        	allDependencies.addAll(getAllDependency(dependency));
		});
        if(allDependencies.contains(task)) {
        	throw new RuntimeException("Detected circular dependency");
        }
        task.setDependencies(allTasks);
        taskRepo.save(task);
    }
    
    public List<Task> getAllDependency(Task task){
    	List<Task> allDependencies = new ArrayList<Task>();
    	allDependencies.addAll(task.getDependencies());
    	task.getDependencies().forEach( dependency -> {
    		getAllDependency(dependency).stream().forEach( innerDependency -> {
    			if(!allDependencies.contains(innerDependency)) {    				
    				allDependencies.add(innerDependency);
    			}
    		});
    	});
    	return allDependencies;
    }
    
    public ProjectPlan generateSchedule(ProjectPlan projectPlan, LocalDate startDate, int resourcePerson) {
    	List<Task> allTasks = taskRepo.findByProjectPlan(projectPlan);
    	Map<Long,Task> startedTasks = new HashMap<Long, Task>();
    	sortTask(allTasks, startedTasks);
        return projectPlan;
    }
    
    List<Task> sortTask(List<Task> allTasks, Map<Long, Task> startedTasks){
//    	allTasks.sort(new Comparator<Task>());
    	return allTasks;
    }
    
}
