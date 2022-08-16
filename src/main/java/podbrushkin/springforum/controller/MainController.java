package podbrushkin.springforum.controller;

import podbrushkin.springforum.service.*;
import podbrushkin.springforum.model.UserDto;
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
import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import org.thymeleaf.context.LazyContextVariable;
import java.util.StringJoiner;
import java.util.List;

@Controller
public class MainController {
	
	@Autowired
	UserService userService;
	
	@ModelAttribute
	public void passPossibleRoles(Model model) {
		model.addAttribute("possibleRoles", userService.getPossibleRoles());
	}
	
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
		model.addAttribute("user", new UserDto());
		// model.addAttribute("possibleRoles", userService.getPossibleRoles());
		return "createUser";
	}
	
	/* @PostMapping("/createUser")
	public String createUser(@ModelAttribute @Valid UserDto user, BindingResult bindingResult) {
		// model.addAttribute("possibleRoles", userService.getPossibleRoles());
		if (bindingResult.hasErrors()) return "createUser";
		userService.createUser(user);
		return "createUser";
	} */
	
	/* @PostMapping("/createUser")
	public String createUser(@ModelAttribute UserDto user) {
		// model.addAttribute("possibleRoles", userService.getPossibleRoles());
		userService.createUser(user);
		return "createUser";
	} */
	
	@PostMapping("/createUser")
	public String createUser(Model model, @ModelAttribute("user") @Valid UserDto user, 
		BindingResult bindingResult) {
			
		if (bindingResult.hasErrors()) return "createUser";
		var freshUser = new UserDto(userService.createUser(user));
		freshUser.setPassword(null);
		model.addAttribute("successMsg", freshUser.toString());
		return "createUser";
	}
	
	@GetMapping("/listUsers")
	public String listUsers(Model model) {
		
		var users = userService.getAllEager().stream()
			.map(u -> {
				var dto = new UserDto(u);
				dto.setPassword(null);
				return dto;
			}).toList();
		
		model.addAttribute("users", users);
		
		
		return "listUsers";
	}
	
	@GetMapping("/login")
	String login() {
		return "login";
	}
}