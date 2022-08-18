package podbrushkin.springforum.service;

import podbrushkin.springforum.model.*;
import podbrushkin.springforum.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;	//oauth

import javax.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import java.math.BigInteger;

@Service
@Transactional(readOnly=true)
@lombok.extern.slf4j.Slf4j
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@PersistenceContext
	EntityManager em;
	
	public List<User> getAll() {
		return userRepository.findAll();
	}
	
	public List<User> getAllEager() {
		var users = userRepository.findAll();
		for (var u : users) {
			u.getRoles().size();
		}
		
		return users;
		
	}
	
	/* public List<User> fetch(List<User> users) {
		for (var u : users) {
			u.getRoles().size();
		}
		return users;
	} */
	
	public Optional<User> findByUsername(String username) {
		return Optional.ofNullable(userRepository.findByUsername(username));
	}
	
	public Optional<User> getById(long id) {
		return Optional.ofNullable(userRepository.getReferenceById(id));
	}
	
	@Transactional
	public User createUser(User user) {
		if (user.getUsername().length() < 3) {
			log.warn("Prevented attempt to create user with small username");
			return null;
		}
		user.setId(null);
		if (user.getPassword() != null)
			user.setPassword(encoder.encode(user.getPassword()));
		var u = userRepository.save(user);
		log.info("Persisted new user to database: " + u);
		return u;
	}
	
	@Transactional
	public User createUser(UserDto userDto) {
		var u = UserDto.toUser(userDto);
		
		return createUser(u);
	}
	
	@Transactional
	public User createUserFromOid(Object userOid) {
		if (!(userOid instanceof DefaultOidcUser)) {
			log.error("Bad principal:"+userOid);
			return null;
		}
		var userOidPrincipal = (DefaultOidcUser) userOid;
		var sub = (String) userOidPrincipal.getAttribute("sub");
		log.info("createUser: sub="+sub);
		
		Long userid = null;
		try {
			var q = em.createNativeQuery("SELECT userid FROM user_openid where openid=:openid");
			q.setParameter("openid", sub);
			var tmp = (BigInteger) q.getSingleResult();
			userid = tmp.longValue();
		} catch (Exception e) {
			if (e.getCause() != null && e.getCause().getCause() != null) {
				var causeOfCause = e.getCause().getCause().toString();
				if (causeOfCause.contains("Table 'springforum.user_openid' doesn't exist")) {
					var upd = em.createNativeQuery("create table user_openid (userid bigint, openid varchar(256) unique)");
					upd.executeUpdate();
				}
			} else log.error(""+e);
		}
		if (userid == null) {
			var newUser = createUser(new User(sub, null, Set.of("ROLE_USER")));
			userid = newUser.getId();
			
			var upd = em.createNativeQuery("insert into user_openid values (:userid, :openid)");
			upd.setParameter("userid", userid);
			upd.setParameter("openid", sub);
			upd.executeUpdate();
			return newUser;
		} else {
			log.info("User have been recognised and wouldn't be created");
			var user = userRepository.getReferenceById(userid);
			user.getRoles().size();
			return user;
			
		}
	}
	
	public List<String> getPossibleRoles() {
		String q = "select distinct(r) from User u join u.roles r";
		var possibleRoles = em.createQuery(q, String.class).getResultList();
		if (possibleRoles.contains(null)) {
			possibleRoles.remove(null);
		}
		return possibleRoles;
	}
}