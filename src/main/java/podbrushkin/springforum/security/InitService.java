package podbrushkin.springforum.security;

import podbrushkin.springforum.model.User;
import org.springframework.web.context.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
// import javax.annotation.PostConstruct;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.ContextRefreshedEvent;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class InitService {
	
	@PersistenceContext
	EntityManager em;
	
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
			em.persist(new User("admin", encoder.encode("qwerty"), "ROLE_ADMIN, ROLE_USER"));
		}
		log.info("List of existing users:"+ users);
	}
	
	@EventListener(ContextRefreshedEvent.class)
	public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(InitService.class).createUserIfThereAreNone();
    }
	
	
}