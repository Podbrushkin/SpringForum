package podbrushkin.springforum.model;

import javax.persistence.*;
import java.util.Set;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

@Entity
@Table(name="users")
@SecondaryTables( value = {
	@SecondaryTable(name = "userid_username", pkJoinColumns=@PrimaryKeyJoinColumn(name="userid", referencedColumnName="id")),
	// @SecondaryTable(name = "userid_role", pkJoinColumns=@PrimaryKeyJoinColumn(name="userid", referencedColumnName="id"))
	// @SecondaryTable(name = "userid_username"),
	// @SecondaryTable(name = "userid_roles")
})
@lombok.ToString
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(table="userid_username")
	@Size(min=3, max=50)
	@Pattern(regexp="^[a-zA-Zа-яА-Я0-9\\s_.-]+$")
	private String username;
	@Size(min=6, max=50)
	@Pattern(regexp="^[\\p{Punct}a-zA-Zа-яА-Я0-9\\s]+$")
	private String password;
	// @Column(table="userid_role")
	@ElementCollection
	@CollectionTable(
		name="userid_role",
		joinColumns=@JoinColumn(name="userid",referencedColumnName="id")
	)
	private Set<String> roles;
	
	public User() {}
	
	public User(String username, String password, Set<String> roles) {
		
		this.username = username;
		this.password = password;
		this.roles = roles;
		
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
	
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof User)) return false;
		var another = (User) obj;
		boolean bothIdsNotNull = (this.getId() != null) && (another.getId() != null);
		return (bothIdsNotNull) ? this.getId().equals(another.getId()) : false;
	}
	
	@Override
	public int hashCode() {
		return this.getId() != null ? this.getId().hashCode() : -1;
	}

	
}