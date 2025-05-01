package nl.capgemini.festival;

import nl.capgemini.festival.role.controller.RoleController;
import nl.capgemini.festival.user.controller.UserController;
import nl.capgemini.festival.userfunction.controller.UserFunctionController;
import nl.capgemini.festival.userfunction.entity.UserFunction;
import nl.capgemini.festival.user.entity.User;
import nl.capgemini.festival.role.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class FestivalApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FestivalApplication.class, args);
	}

	@Autowired
	private UserController userController;
	@Autowired
	private RoleController roleController;
	@Autowired
	private UserFunctionController userFunctionController;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void run(String... args) {
		Role user = new Role("USER", "Default user role.");
		Role admin = new Role("ADMIN", "Administrator role.", 1);
		roleController.save(user);
		roleController.save(admin);

		UserFunction mechanic = new UserFunction("ENGINEER", "Mechanic");
		UserFunction sales = new UserFunction("SALES", "Sales administrator.");
		UserFunction boss = new UserFunction("BOSS", "Owner of the company.");
		userFunctionController.save(mechanic);
		userFunctionController.save(sales);
		userFunctionController.save(boss);

		User user1 = new User("user", "User1Pass!", "01-01-2000");
		User user2 = new User("admin", "Admin1Pass!", "01-01-2000", roleController.getHighestAccessRole(), userFunctionController.findByName("BOSS"));
		User user3 = new User("bossman", "Boss1Pass!", "bossman@email.com", "12-03-1973", user, boss);
		userController.save(user1);
		userController.save(user2);
		userController.save(user3);
	}
}
