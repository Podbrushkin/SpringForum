package podbrushkin.springforum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.servlet.ModelAndView;
import java.util.StringJoiner;

@Controller
public class MainController {
	
	@GetMapping("/")
	public ModelAndView root() {
		var mav = new ModelAndView("basicPage");
		// mav.addObject("title", "Welcome");
		return mav;
	}
	
	@GetMapping("/info")
	public ModelAndView userInfo(Authentication authentication) {
		
		// if (authentication == null) return var mav = new ModelAndView("basicpage");
		// "You are not logged in.";
		String msg = "Hello " + authentication.getName()+", here are your roles: ";
		var sj = new StringJoiner(", ", "", ".");
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			sj.add(authority.getAuthority());
		}
		msg += sj.toString();
		// return msg;
		
		var mav = new ModelAndView("basicPage");
		mav.addObject("title", "Info");
		mav.addObject("text", msg);
		return mav;
		
	}
	
}