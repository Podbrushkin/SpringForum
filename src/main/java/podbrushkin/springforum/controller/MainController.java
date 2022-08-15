package podbrushkin.springforum.controller;

import podbrushkin.springforum.service.*;
import podbrushkin.springforum.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import java.util.StringJoiner;

@Controller
public class MainController {
	
	@Autowired
	UserService userService;
	
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
	
	@GetMapping("/createUser")
	public String createUser(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("possibleRoles", userService.getPossibleRoles());
		return "createUser";
	}
	
	@PostMapping("/createUser")
	public String createUser(@ModelAttribute User user) {
		
		userService.createUser(user);
		return "createUser";
	}
	
}