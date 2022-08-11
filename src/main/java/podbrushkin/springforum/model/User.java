package podbrushkin.springforum.model;

import javax.persistence.*;

@Entity
@Table(name="users")
@SecondaryTables( value = {
	@SecondaryTable(name = "userid_username", pkJoinColumns=@PrimaryKeyJoinColumn(name="userid", referencedColumnName="id")),
	@SecondaryTable(name = "userid_roles", pkJoinColumns=@PrimaryKeyJoinColumn(name="userid", referencedColumnName="id"))
	// @SecondaryTable(name = "userid_username"),
	// @SecondaryTable(name = "userid_roles")
})
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(table="userid_username")
	private String username;
	private String password;
	@Column(table="userid_roles")
	private String roles;
	
	public User() {}
	
	public User(String username, String password, String roles) {
		
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
	
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
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