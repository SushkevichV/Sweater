package by.sva.sweater.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import javax.sql.DataSource; // заменили на UserService
import by.sva.sweater.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	/* заменяем DataSource на UserService
	@Autowired
	private DataSource dataSource;
	*/
	
	@Autowired
	private UserService userService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http
			.authorizeRequests()
			.antMatchers("/", "/registration", "/static/**, /activate/*").permitAll() // страницы, к которым открыт доступ
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.loginPage("/login")
			.permitAll()
			.and()
			.rememberMe() // запомнить меня
			.and()
			.logout()
			.permitAll();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		/* заменяем jdbcAuthentication() на userDetailsService
		auth.jdbcAuthentication()
				.dataSource(dataSource)
				.passwordEncoder(NoOpPasswordEncoder.getInstance())
				.usersByUsernameQuery("select username, password, active from usr where username=?")
				// получить username и role из таблицы usr и связанной с ней таблицы roles через поля id и user_id
				.authoritiesByUsernameQuery("select u.username, ur.roles from usr u inner join user_role ur on u.id = ur.user_id where u.username=?");
		*/
		
		auth.userDetailsService(userService)
				.passwordEncoder(NoOpPasswordEncoder.getInstance());
	}

}
