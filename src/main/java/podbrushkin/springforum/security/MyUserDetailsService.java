package podbrushkin.springforum.security;

import podbrushkin.springforum.model.User;

import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.*;

@Component
@Transactional(readOnly=true)
@lombok.extern.slf4j.Slf4j
public class MyUserDetailsService implements UserDetailsService {
	
	@PersistenceContext
	EntityManager em;
	
	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		
		var jpql = "from User u where u.username=:uname";
		var query = em.createQuery(jpql, User.class);
		query.setParameter("uname", s);
		var foundUsers = query.getResultList();
		
		
		String msg = "";
		if (foundUsers.size() == 0) {
			msg = "Found no user with username=";
			log.warn(msg+s);
			throw new UsernameNotFoundException(msg+s);
		} else if (foundUsers.size() == 0) {
			msg = "Found multiple users with username=";
			log.warn(msg+s);
			throw new UsernameNotFoundException(msg+s);
		}
		else {
			log.info("Found User:" + foundUsers.get(0));
			return new MyUserDetails(foundUsers.get(0));
		}
		
	}
	
	
	
}