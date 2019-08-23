package com.mahvine.vlocity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import de.vandermeer.asciitable.AsciiTable;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	Scanner scanner = new Scanner(System.in);
	final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
		        	  log.info("----All tasks------------------");
		        	  taskRepo.findByProjectPlan(project).forEach(allTask -> {
		        		  List<Long> depIds = allTask.getDependencies().stream().map( taskDep -> taskDep.getId()).collect(Collectors.toList());
		        		  log.info("{} - {} ({} days)| Dependencies: {}", allTask.getId(), allTask.getName(), allTask.getDurationInDays(), StringUtils.collectionToDelimitedString(depIds, ","));
		        	  });
				      log.info("Enter to continue...");
				      scanner.nextLine();
				      clearScreen();
		          } else if(choice.equalsIgnoreCase("3")) {
				      clearScreen();
		        	  updateTask(scanner, project, taskRepo, projectService);		              
		          } else if(choice.equalsIgnoreCase("4")) {
		        	  LocalDate startDate = null;

		        	  do {		        		  
		        		  log.info("Enter project start date: (Now)/yyyy-MM-dd");
		        		  String startDateStr = scanner.nextLine();
		        		  if(startDateStr.isEmpty()) {
		        			  startDate = LocalDate.now();
		        		  } else {
		        			  try {		        				  
		        				  startDate = LocalDate.parse(startDateStr, formatter);
		        			  } catch(Exception e) {
		        				  log.error("Invalid date");
		        			  }
		        		  }
		        	  } while(startDate == null);
		        	  project = projectPlanRepo.getOne(project.getId());
		        	  project = projectService.generateSchedule(project, startDate);
		        	  
		        	  displayProjectSchedule(project);
		        	  log.info("Press any key to continue...");
	        		  scanner.nextLine();
		          } else if(choice.equalsIgnoreCase("M")) {
		        	  project = null;
		          } else if(choice.equalsIgnoreCase("X")) {
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

		          } else if(choice.equalsIgnoreCase("X")) {
		          } else {
		              log.info("Invalid choice");   
		          }		          
		      }
		    } while(!choice.equalsIgnoreCase("X"));
			

		      log.info("-------------------------------");
		      log.info("Application closed...");
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
                scanner.nextLine(); //workaround
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

        log.info("Set task dependencies(Comma separated ids/Can leave empty):");
        String dependencyIds = scanner.nextLine();
        List<Long> taskIds = StringUtils.commaDelimitedListToSet(dependencyIds).stream()
            .map( id -> Long.parseLong(id)).collect(Collectors.toList());
        if(!taskIds.isEmpty()) {
        	try {        		
        		projectService.setDependency(task, taskIds);
        	} catch(Exception e) {
        		log.error(e.getMessage());
        	}
        }
	}
	

	public static void updateTask(Scanner scanner, ProjectPlan project, TaskRepository taskRepo, ProjectPlanService projectService){

	    log.info("----Select task to update -----------");
        List<Task> allTasks = taskRepo.findByProjectPlan(project);
        allTasks.forEach( allTask -> {
            log.info(allTask.getId()+" - "+allTask.getName());
        });
	    Task task = null;
	    do {
	    	try {
	    		log.info("Select task ID:");
	    		Long id = scanner.nextLong();
	    		task = taskRepo.getOne(id);
	    		scanner.nextLine(); //workaround
	    	}catch(Exception e) {
	    		log.error(e.getMessage());
	    	}
	    } while(task==null);
        log.info("Enter task name ({}):",task.getName());
        String name = scanner.nextLine();
        if(!name.isEmpty()) {        	
        	task.setName(name);
        }
        do {
            log.info("Enter task duration in days({}):",task.getDurationInDays());
            try{
                String durationStr = scanner.nextLine();
                if(!durationStr.isEmpty()) {
                	task.setDurationInDays(Integer.parseInt(durationStr));
                }
            } catch(Exception e) {
            }
        } while(task.getDurationInDays() <= 0);
        taskRepo.save(task);
        
        
        log.info("----Select Dependencies-------------");
        final Long taskId = task.getId();
        allTasks.forEach( allTask -> {
        	if(!allTask.getId().equals(taskId)) {
        		log.info(allTask.getId()+" - "+allTask.getName());
        	}
        });
        List<Long> dependencies = task.getDependencies().stream().map(dependency -> dependency.getId()).collect(Collectors.toList());
        log.info("Set task dependencies({}/leave empty to remove dependencies):", StringUtils.collectionToDelimitedString(dependencies, ","));
        String dependencyIds = scanner.nextLine();
        List<Long> taskIds = StringUtils.commaDelimitedListToSet(dependencyIds).stream()
            .map( id -> Long.parseLong(id)).collect(Collectors.toList());
    	try {        		
    		projectService.setDependency(task, taskIds);
    	} catch(Exception e) {
    		log.error(e.getMessage());
    	}
	}
	
	public static void clearScreen() {  
        System.out.print("\033[H\033[2J");
        System.out.flush();  
    }
	
	public void displayProjectSchedule(ProjectPlan project) {
		AsciiTable at = new AsciiTable();
		at.addRule();
		at.addRow(null,null,null,"Project: "+project.getTitle()+" Schedule");
		at.addRule();
		at.addRow("Tasks", "Dependency", "Start Date", "End Date");
		at.addRule();
		int i = 1;
		for(Task task: project.getTasks()) {
			List<Long> dependencies = task.getDependencies().stream().map(dependency -> dependency.getId()).collect(Collectors.toList());
	        String dependencyIds = dependencies.isEmpty() ? "None" : StringUtils.collectionToDelimitedString(dependencies, ",");
			at.addRow(task.getId()+" - "+task.getName(), dependencyIds, formatter.format(task.getStartDate()), formatter.format(task.getEndDate()));
			at.addRule();
			i++;
		}
	    log.info(at.render());
	}

}