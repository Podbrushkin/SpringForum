package podbrushkin.springforum.service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;	//oauth
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import podbrushkin.springforum.model.User;
import podbrushkin.springforum.model.UserDto;
import podbrushkin.springforum.repository.UserRepository;
import podbrushkin.springforum.security.MyUserDetails;

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
	
	public User getOrCreateUserByOidcPrincipal(Object principal) {
		if (!(principal instanceof DefaultOidcUser)) {
			log.error("Bad principal:"+principal);
			return null;
		}
		var userOidPrincipal = (DefaultOidcUser) principal;
		var user = getUserByOidcPrincipal(userOidPrincipal);
		if (user != null) return user;
		else return createUserByOidcPrincipal(userOidPrincipal);
	}
	
	@Transactional
	private User createUserByOidcPrincipal(DefaultOidcUser oidUser) {

		var sub = (String) oidUser.getAttribute("sub");
		
		var newUser = createUser(new User(sub, null, Set.of("ROLE_USER")));
		var userid = newUser.getId();
		
		var upd = em.createNativeQuery("insert into user_openid values (:userid, :openid)");
		upd.setParameter("userid", userid);
		upd.setParameter("openid", sub);
		upd.executeUpdate();
		
		var msg = "Created new user by OpenId: userId=%s, username=%s, openId=%s";
		log.info(String.format(msg, newUser.getId(), newUser.getUsername(), sub));
		return newUser;
	
	}
	
	private User getUserByOidcPrincipal(DefaultOidcUser principal) {
		var sub = (String) principal.getAttribute("sub");
		
		Long userid = null;
		try {
			var q = em.createNativeQuery("SELECT userid FROM user_openid where openid=:openid");
			q.setParameter("openid", sub);
			var id = (BigInteger) q.getSingleResult();
			userid = id.longValue();
			var user = userRepository.getReferenceById(userid);
			user.getRoles().size();
			var msg = "Found user by OpenId: userId=%s, username=%s, openId=%s";
			log.info(String.format(msg, user.getId(), user.getUsername(), sub));
			return user;
		} catch (Exception e) {
			//TODO: get rid of this
			if (e.getCause() != null && e.getCause().getCause() != null) {
				var causeOfCause = e.getCause().getCause().toString();
				if (causeOfCause.contains("Table 'springforum.user_openid' doesn't exist")) {
					var upd = em.createNativeQuery("create table user_openid (userid bigint, openid varchar(256) unique)");
					upd.executeUpdate();
				} else log.error("Failed to read user from DB by openId"+e);
			}
		}
		return null;
	}
	@Transactional
	public void createTableIfNotExists() {
        Query query = em.createNativeQuery(
            "SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'user_openid')"
        );
		BigInteger result = (BigInteger) query.getSingleResult();
		log.info("isTableExists(): "+result);
		if (!result.equals(BigInteger.ONE)) {
			var upd = em.createNativeQuery("create table user_openid (userid bigint, openid varchar(256) unique)");
			upd.executeUpdate();
		}
    }
	
	@Transactional(readOnly=false)
	public void changeUsername(User user, String newUsername) {
		if (userRepository.findByUsername(newUsername) != null) return;
		var msg = "Changing username: %s, to: %s"; 
		log.info(String.format(msg, user, newUsername));
		user.setUsername(newUsername);
		em.merge(user);
	}
	
	@Transactional(readOnly=false)
	public void changeUsernameOfPrincipal(Object principal, String newUsername) {
		var userDet = (MyUserDetails) principal;
		changeUsername(userRepository.findByUsername(userDet.getUsername()), newUsername);
		
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