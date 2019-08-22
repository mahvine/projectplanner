package com.mahvine.vlocity;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import com.mahvine.vlocity.model.ProjectPlan;
import com.mahvine.vlocity.model.Task;
import com.mahvine.vlocity.repository.ProjectPlanRepository;
import com.mahvine.vlocity.repository.TaskRepository;
import com.mahvine.vlocity.service.ProjectPlanService;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Bean
	public CommandLineRunner demo(ProjectPlanRepository projectPlanRepo, TaskRepository taskRepo, ProjectPlanService projectService) {
		return (args) -> {
		    String choice = null;
		    ProjectPlan project = null;
		    do {
		      if(project == null){
		      log.info("----MAIN MENU------------------");
		      log.info("1 - Create Project Plan");
		      log.info("2 - Show Project Plan");
		      log.info("3 - Select Project Plan");		          
		      } else {
		      log.info("-------------------------------");
		      log.info("Project: "+project.getTitle());
		      log.info("-------------------------------");
		      log.info("1 - Create Task");
		      log.info("2 - Show Tasks");
		      log.info("3 - Update Task");
		      log.info("4 - Generate project schedule");
		      log.info("M - Back to MAIN MENU");
		      }
		      log.info("X - Exit");
		      log.info("-------------------------------");
		      log.info("Enter choice:");
		      choice = scanner.nextLine();
		      
		      if(project != null){
		          if(choice.equalsIgnoreCase("1")) {
        		      generateTask(scanner, project, taskRepo, projectService);
		              log.info("Successfully created new task!");
		              
		          } else if(choice.equalsIgnoreCase("2")) {
		              
		          } else if(choice.equalsIgnoreCase("3")) {
		          } else if(choice.equalsIgnoreCase("M")) {
		              project = null;
		          } else {
		              log.info("Invalid choice");   
		          }
		      } else {
		          if(choice.equalsIgnoreCase("1")) {
        		      log.info("Enter project title:");
		              String title = scanner.nextLine();
		              ProjectPlan projectPlan = new ProjectPlan();
		              projectPlan.setTitle(title);
		              projectPlanRepo.save(projectPlan);
		              log.info("Successfully created new project plan!");
		          } else if(choice.equalsIgnoreCase("2")) {
        		      log.info("----All Projects---------------");
        		      projectPlanRepo.findAll().forEach(projectPlan -> {
            		      log.info(projectPlan.getId()+" - "+projectPlan.getTitle());
        		      });
		          } else if(choice.equalsIgnoreCase("3")) {
        		      log.info("----Select Project-------------");
        		      projectPlanRepo.findAll().forEach(projectPlan -> {
            		      log.info(projectPlan.getId()+" - "+projectPlan.getTitle());
        		      });
        		      do {
        		          try{     		              
                		      log.info("Enter project ID:");
        		              Long id = scanner.nextLong();
        		              project = projectPlanRepo.getOne(id);
        		              project.getTitle();
        		          } catch(Exception e) {
        		              log.error("Error:"+e.getMessage());
        		          }
        		      } while(project == null);
		              log.info("Successfully selected project: "+project.getTitle());
		          } else {
		              log.info("Invalid choice");   
		          }		          
		      }
		    } while(!choice.equalsIgnoreCase("X"));
			
			
		};
	}
	
	public static void generateTask(Scanner scanner, ProjectPlan project, TaskRepository taskRepo, ProjectPlanService projectService){	    
        Task task = new Task();
        log.info("Enter task name:");
        String name = scanner.nextLine();
        task.setName(name);
        do {
            log.info("Enter task duration in days:");
            try{     		              
                int duration = scanner.nextInt();
                  task.setDurationInDays(duration);
            } catch(Exception e) {
            }
        } while(task.getDurationInDays() <= 0);
        task.setProjectPlan(project);
        taskRepo.save(task);
        
        
        log.info("----Select Dependencies-------------");
        List<Task> allTasks = taskRepo.findByProjectPlan(project);
        
        allTasks.forEach( allTask -> {
            log.info(allTask.getId()+" - "+allTask.getName());
        });

        log.info("Select task dependency/s(comma separated):");
        String dependencyIds = scanner.nextLine();
        List<Long> taskIds = StringUtils.commaDelimitedListToSet(dependencyIds).stream()
            .map( id -> Long.parseLong(id)).collect(Collectors.toList());
        projectService.addDependency(task, taskIds);
	}
	
	public static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

}