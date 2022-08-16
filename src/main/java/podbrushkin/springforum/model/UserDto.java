package podbrushkin.springforum.model;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;


@lombok.ToString
// @org.springframework.stereotype.Component
public class UserDto {
	
	// @Autowired
	// PasswordEncoder encoder;
	
	private Long id;
	@Size(min=3, max=50)
	@Pattern(regexp="^[a-zA-Zа-яА-Я0-9\\s_.-]+$")
	private String username;
	
	@Size(min=6, max=50)
	@Pattern(regexp="^[\\p{Punct}a-zA-Zа-яА-Я0-9\\s]+$")
	private String password;
	@NotEmpty
	private Collection<String> roles;
	
	public UserDto() {}
	
	public UserDto(String username, String password, Set<String> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}
	
	public UserDto(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.roles = user.getRoles();
	}
	
	public static UserDto toDto(User user) {
		return new UserDto(user);
	}
	
	public static User toUser(UserDto userDto) {
		var u = new User(userDto.getUsername(), userDto.getPassword(), new HashSet<String>(userDto.getRoles()));
		if (userDto.getId() != null) u.setId(userDto.getId());
		return u;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Collection<String> getRoles() {
		return roles;
	}
	public void setRoles(Collection<String> roles) {
		this.roles = roles;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof UserDto)) return false;
		var another = (UserDto) obj;
		boolean bothIdsNotNull = (this.getId() != null) && (another.getId() != null);
		return (bothIdsNotNull) ? this.getId().equals(another.getId()) : false;
	}
	
	@Override
	public int hashCode() {
		return this.getId() != null ? this.getId().hashCode() : -1;
	}

	
}