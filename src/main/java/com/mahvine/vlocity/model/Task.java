package com.mahvine.vlocity.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Task {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private String name;
    
    @OneToMany
    private List<Task> dependencies = new ArrayList<Task>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectPlan projectPlan;
    
    private int durationInDays;
    
    
    public Long getId(){
        return id;
    }
    
    public void setId(Long id){
        this.id = id;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public List<Task> getDependencies(){
        return dependencies;
    }
    
    public void setDependencies(List<Task> dependencies){
        this.dependencies = dependencies;
    }
    
    public ProjectPlan getProjectPlan(){
        return projectPlan;
    }
    
    public void setProjectPlan(ProjectPlan projectPlan){
        this.projectPlan = projectPlan;
    }
    
    public int getDurationInDays(){
        return durationInDays;
    }
    
    public void setDurationInDays(int durationInDays){
        this.durationInDays = durationInDays;
    }
    
}
