package dev.jaqs.cruddemo;

import dev.jaqs.cruddemo.dao.AppDAO;
import dev.jaqs.cruddemo.entity.Instructor;
import dev.jaqs.cruddemo.entity.InstructorDetail;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CruddemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CruddemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(AppDAO appDAO) {
		return runner -> {
			// createInstructor(appDAO);
			// findInstructor(appDAO);
			deleteInstructor(appDAO);
		};
	}

	private void createInstructor(AppDAO appDAO) {
		/*
		Instructor instructor = new Instructor(
				"Nata",
				"Tovar",
				"ntcantor@luv2code.com");

		InstructorDetail instructorDetail = new InstructorDetail("http://ntcantor.com/youtube", "Cooking");
		*/

		Instructor instructor = new Instructor(
				"Judith",
				"Sanabria",
				"jsf@luv2code.com");

		InstructorDetail instructorDetail = new InstructorDetail("http://jsf.com/youtube", "Teaching");

		instructor.setInstructorDetail(instructorDetail); // Associate the independent objects

		System.out.println("Saving instructor: " + instructor);
		appDAO.saveInstructor(instructor); // this will ALSO save the details object
		System.out.println("Done!");
	}

	private void findInstructor(AppDAO appDAO) {
		Long id= 2L;
		System.out.println("Finding instructor with id: " + id);
		Instructor instructor = appDAO.findInstructorById(id);
		System.out.println(instructor);
		System.out.println(instructor.getInstructorDetail());
	}

	private void deleteInstructor(AppDAO appDAO) {
		Long id= 2L;
		System.out.println("Deleting instructor with id: " + id);
		appDAO.deleteInstructorById(id);
		System.out.println("Done!");
	}

}
