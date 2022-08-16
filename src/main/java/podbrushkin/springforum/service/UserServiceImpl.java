package podbrushkin.springforum.service;

import podbrushkin.springforum.model.*;
import podbrushkin.springforum.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.List;

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
	
	public List<String> getPossibleRoles() {
		String q = "select distinct(r) from User u join u.roles r";
		var possibleRoles = em.createQuery(q, String.class).getResultList();
		if (possibleRoles.contains(null)) {
			possibleRoles.remove(null);
		}
		return possibleRoles;
	}
}