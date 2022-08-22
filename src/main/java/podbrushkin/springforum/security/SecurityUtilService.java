package podbrushkin.springforum.security;

import podbrushkin.springforum.model.User;
import podbrushkin.springforum.service.UserService;
import podbrushkin.springforum.security.MyUserDetails;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
// import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class SecurityUtilService {
	
	@PersistenceContext
	EntityManager em;
	@Autowired
	UserService userService;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Transactional(readOnly=false)
	public void createUserIfThereAreNone() {
		log.info("Checking if there are any users in database...");
		// var jpql = "select count(*) from User";
		var jpql = "from User";
		var query = em.createQuery(jpql, User.class);
		var users = query.getResultList();
		
		if (users.size() == 0) {
			log.info("no users found, creating admin-qwerty");
			// sf.openSession().persist(new User("admin", encoder.encode("qwerty"), "ROLE_ADMIN, ROLE_USER"));
			em.persist(new User("admin", encoder.encode("qwerty"), Set.of("ROLE_ADMIN","ROLE_USER")));
		}
		log.info("List of existing users:"+ users);
	}
	
	public boolean reauthenticateOidcUser(Object principal) {
		if (!(principal instanceof DefaultOidcUser)) {
			log.warn("Attempt to reauthenticate bad principal: "+principal);
			return false;
		}
		var user = userService.getOrCreateUserByOidcPrincipal(principal);
		var newAuth = new UsernamePasswordAuthenticationToken(new MyUserDetails(user), null, 
			user.getRoles().stream()
				.map(s -> new SimpleGrantedAuthority(s.strip())).toList());
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		return true;
	}
	
	public boolean reauthenticateUser(Object principal, String newUsername) {
		if (!(principal instanceof MyUserDetails)) {
			log.warn("Attempt to reauthenticate bad principal: "+principal);
			return false;
		}
		var userDet = (MyUserDetails) principal;
		var newUserDet = new MyUserDetails(userService.findByUsername(newUsername).get());
		var newAuth = new UsernamePasswordAuthenticationToken(newUserDet, userDet.getPassword(), 
			userDet.getAuthorities());
		log.info("newAuth:"+newAuth);
		SecurityContextHolder.getContext().setAuthentication(newAuth);
		return true;
		
	}
	
	@EventListener(ContextRefreshedEvent.class)
	public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(SecurityUtilService.class).createUserIfThereAreNone();
    }
	
	
}