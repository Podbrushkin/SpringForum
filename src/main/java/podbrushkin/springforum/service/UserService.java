package podbrushkin.springforum.service;

// import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import podbrushkin.springforum.model.*;
import java.util.Optional;
import java.util.List;

public interface UserService {
	
	List<User> getAll();
	List<User> getAllEager();
	Optional<User> findByUsername(String username);
	Optional<User> getById(long id);
	User createUser(User user);
	User createUser(UserDto user);
	User getOrCreateUserByOidcPrincipal(Object userOid);
	List<String> getPossibleRoles();
	// List<User> fetch(List<User> users);
}