package podbrushkin.springforum.security;

import podbrushkin.springforum.model.User;
import org.springframework.web.context.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.annotation.PostConstruct;

// @ApplicationScope
@Service
@Transactional
@lombok.extern.slf4j.Slf4j
public class Initializer {
	
	// @PersistenceContext
	// EntityManager em;
	
	@Autowired
	org.hibernate.SessionFactory sf;
	
	@Autowired
	PasswordEncoder encoder;
	
	
	@PostConstruct
	@Transactional(readOnly=false)
	public void init() {
		log.info("Checking if there are any users in database...");
		var jpql = "select count(*) from User";
		var query = sf.openSession().createQuery(jpql, Long.class);
		var usersAmount = query.getSingleResult();
		
		if (usersAmount == 0) {
			log.info("no users found, creating admin-qwerty");
			sf.openSession().persist(new User("admin", encoder.encode("qwerty"), "ROLE_ADMIN, ROLE_USER"));
		}
	}
	
	
}