package podbrushkin.springforum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.StringJoiner;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String root() {
		return "welcome";
	}
	
	@GetMapping("/info")
	public @ResponseBody String userInfo(Authentication authentication) {
		if (authentication == null) return "You are not logged in.";
		String msg = "Hello " + authentication.getName()+", here are your roles: ";
		var sj = new StringJoiner(", ", "", ".");
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			sj.add(authority.getAuthority());
		}
		msg += sj.toString();
		return msg;
	}
	
}