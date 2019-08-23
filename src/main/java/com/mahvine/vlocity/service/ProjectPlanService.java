package com.mahvine.vlocity.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    
    public ProjectPlan generateSchedule(ProjectPlan projectPlan, LocalDate startDate) {
    	List<Task> allTasks = taskRepo.findByProjectPlan(projectPlan);
    	Map<Long,Task> completedTasks = new HashMap<Long, Task>();
    	do {
    		Task readyTask = extractReadyTask(allTasks, completedTasks);
    		allTasks.remove(readyTask);
    		LocalDate earliestStart = getEarliestStart(readyTask, completedTasks, startDate);
    		readyTask.setStartDate(earliestStart);
    		readyTask.setEndDate(earliestStart.plusDays(readyTask.getDurationInDays() - 1));
    		completedTasks.put(readyTask.getId(), readyTask);
    	} while(!allTasks.isEmpty());
    	List<Task> completedList = new ArrayList<>(completedTasks.values()); 
    	Collections.sort(completedList, new Comparator<Task>() {
			@Override
			public int compare(Task task1, Task task2) {
				return task1.getStartDate().compareTo(task2.getStartDate());
			}
    	});
    	projectPlan.setTasks(completedList);
    	return projectPlan;
    }
    
    
    private Task extractReadyTask(List<Task> tasks, Map<Long, Task> completedTasks) {
    	for(Task task : tasks) {
    		boolean ready = true;
    		for(Task dependency: task.getDependencies()) {
    			if(!completedTasks.containsKey(dependency.getId())){
    				ready = false;
    			}
    		}
    		if(ready) {
    			return task;
    		}
    	}
    	
    	return null;
    }
    
    private LocalDate getEarliestStart(Task readyTask, Map<Long, Task> completedTasks, LocalDate projectStartDate) {
    	LocalDate earliestStart = projectStartDate;
    	for(Task dependency: readyTask.getDependencies()) {
			Task completedTask = completedTasks.get(dependency.getId());
			if(completedTask != null) {
				if(!earliestStart.isAfter(completedTask.getEndDate())) {
					earliestStart = completedTask.getEndDate().plusDays(1);
				}
			}
		}
    	return earliestStart;
    }
    
}
