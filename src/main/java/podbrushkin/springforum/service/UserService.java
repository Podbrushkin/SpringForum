package podbrushkin.springforum.service;

import podbrushkin.springforum.model.*;
import java.util.Optional;
import java.util.List;

public interface UserService {
	
	List<User> getAll();
	Optional<User> findByUsername(String username);
	Optional<User> getById(long id);
	void createUser(User user);
	void createUser(UserDto user);
	List<String> getPossibleRoles();
}