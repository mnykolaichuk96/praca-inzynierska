package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.config;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.security.EmployeeUserDetailService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.security.WorkshopUserDetailService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.EmployeeService;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class DemoSecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private WorkshopService workshopService;

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;


		@Override
		protected void configure(HttpSecurity http) throws Exception {    //HttpSecurity використовується для конфігурації spraing security

			http.authorizeRequests()    // обмежуємо доступ на основі входящого httpServletRequest
					.antMatchers("/").permitAll()           // тут ми вказуємо до яких мепінгів будуть мати доступ які ролі
					.antMatchers("/register/**").permitAll()
					.antMatchers("/employee/**").hasRole("EMPLOYEE")    // /** -- all sub-directories
					.antMatchers("/workshop/**").hasRole("WORKSHOP")
					//.hasAnyAuthority()	дозволяє перечислити ролі для доступу
					.and()
					.formLogin()		//customise log in proces видозмінюємо його під наші потреби
						.defaultSuccessUrl("/home")    // якщо все пройшло успішно редірект на сукцес page
						.loginPage("/login")    // показуємо логування по request mapping	//для цього мапінгу треба писати контроллер
						.failureUrl("/login?error=true")

							//	.loginProcessingUrl("/authenticateTheUser") // логування постить дпні на цей url
					// для цього контроллера не треба він сам перевіряє чи сходяться user id oraz paasword
					.successHandler(customAuthenticationSuccessHandler)
					.permitAll()    // дозволяємо бачити цю форму всім користувачам включно з незареєстрованими
					.and()
					.logout().permitAll()	//дозволяється доступ до фкнкціоналу log out для всіх користувачів
//					.defaultLogoutSuccessHandlerFor()
					.and()
					.exceptionHandling().accessDeniedPage("/access-denied");    // мепінг на метод з вьорсткою для помилки аутентикації

		}

	@Bean
	public DaoAuthenticationProvider authenticationProviderWorkshop() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(workshopUserDetailService()); //set the custom user details service
		//ми вказуєм метод кодування щоб при логуванні спрінг знав як закодувати пароль і міг порівняти з тим що в базі
		auth.setPasswordEncoder(passwordEncoder); //set the password encoder - bcrypt
		return auth;
	}

	@Bean
	public DaoAuthenticationProvider authenticationProviderEmployee() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(employeeUserDetailService()); //set the custom user details service
		auth.setPasswordEncoder(passwordEncoder); //set the password encoder - bcrypt
		return auth;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProviderEmployee())
				.authenticationProvider(authenticationProviderWorkshop());
	}

	@Bean
	public EmployeeUserDetailService employeeUserDetailService() {
			return new EmployeeUserDetailService();
	}

	@Bean
	public WorkshopUserDetailService workshopUserDetailService() {
		return new WorkshopUserDetailService();
	}
}






