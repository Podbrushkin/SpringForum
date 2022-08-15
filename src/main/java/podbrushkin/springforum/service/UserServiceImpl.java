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
	
	public Optional<User> findByUsername(String username) {
		return Optional.ofNullable(userRepository.findByUsername(username));
	}
	
	public Optional<User> getById(long id) {
		return Optional.ofNullable(userRepository.getReferenceById(id));
	}
	
	@Transactional
	public void createUser(User user) {
		user.setId(null);
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
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