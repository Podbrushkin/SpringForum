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
public class MyUserDetailsService implements UserDetailsService {
	
	@PersistenceContext
	EntityManager em;
	
	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		
		var jpql = "from User u where u.username=:uname";
		var query = em.createQuery(jpql, User.class);
		query.setParameter("uname", s);
		var user = query.getSingleResult();
		if (user == null) throw new UsernameNotFoundException("No user with username="+s);
		
		return new MyUserDetails(user);
		
		
	}
	
	
	
}