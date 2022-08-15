package podbrushkin.springforum.security;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		
		auth.userDetailsService(userDetailsService);
		
		/* auth.inMemoryAuthentication()
			.withUser("user").password(encoder.encode("password")).roles("USER")
			.and().withUser("admin").password(encoder.encode("password")).roles("USER", "ADMIN"); */
			
		
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
				.antMatchers("/login").permitAll()
				.antMatchers("/info").permitAll()
				.antMatchers("/createUser").hasAuthority("ROLE_ADMIN")
				.antMatchers("/").permitAll()
				.and().formLogin()
				.and().logout().logoutSuccessUrl("/");
	}
}