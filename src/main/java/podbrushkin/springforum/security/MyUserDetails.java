package podbrushkin.springforum.security;

import podbrushkin.springforum.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Arrays;
import java.util.Collection;


public class MyUserDetails implements UserDetails {
	
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;
    // private static final long serialVersionUID = 1L;
    // private User user;
	
    public MyUserDetails(User user) {
		
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = true;
        // this.authorities = Arrays.stream(user.getRoles().split(","))
			// .map(s -> new SimpleGrantedAuthority(s.strip())).toList();
		
		this.authorities = user.getRoles().stream()
			.map(s -> new SimpleGrantedAuthority(s.strip())).toList();
    }


    /* public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    } */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}