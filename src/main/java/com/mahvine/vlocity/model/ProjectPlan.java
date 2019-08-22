package com.mahvine.vlocity.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class ProjectPlan {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    private String title;
    
    @OneToMany(
        mappedBy = "projectPlan",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Task> tasks;
    
    
    public Long getId(){
        return id;
    }
    
    public void setId(Long id){
        this.id = id;
    }
    
    public String getTitle(){
        return title;
    }
    
    public void setTitle(String title){
        this.title = title;
    }
    
    public List<Task> getTasks(){
        return tasks;
    }
    
    public void setTasks(List<Task> tasks){
        this.tasks = tasks;
    }
    
    
    
}
