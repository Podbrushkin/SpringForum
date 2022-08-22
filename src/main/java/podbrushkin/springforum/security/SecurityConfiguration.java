package podbrushkin.springforum.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
// oauth2
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
@Slf4j
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
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
				.antMatchers("/login").permitAll()
				.antMatchers("/info").permitAll()
				.antMatchers("/createUser").hasAuthority("ROLE_ADMIN")
				.antMatchers("/listUsers").hasAuthority("ROLE_ADMIN")
				.antMatchers("/profile/**").authenticated()
				.antMatchers("/").permitAll()
				.and().formLogin().loginPage("/login")
				.and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/")
				.and().oauth2Login()
				.loginPage("/login")
				.defaultSuccessUrl("/oauth_success")
				.clientRegistrationRepository(clientRegistrationRepository())
				.authorizedClientService(authorizedClientService());
				
	}
	
	@Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = Stream.of("google")
          .map(c -> getRegistration(c))
          .filter(registration -> registration != null)
          .collect(Collectors.toList());
        log.info("clientRegistrations: "+registrations);
        return new InMemoryClientRegistrationRepository(registrations);
    }
	
	private static String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";

	@Autowired
	private Environment env;

	private ClientRegistration getRegistration(String client) {
		String clientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");
		if (clientId == null) {
			return null;
		}

		String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-secret");
	 
		if (client.equals("google")) {
			var googleOauth = CommonOAuth2Provider.GOOGLE.getBuilder(client)
				.clientId(clientId).clientSecret(clientSecret)
				.scope("openid").build();
				
			log.info("googleOauth="+googleOauth);
			return googleOauth;
		}
		return null;
	}
	
	@Bean
	public OAuth2AuthorizedClientService authorizedClientService() {
	 
		return new InMemoryOAuth2AuthorizedClientService(
		  clientRegistrationRepository());
	}
}