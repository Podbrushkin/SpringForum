package podbrushkin.springforum.controller;

import java.util.StringJoiner;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import podbrushkin.springforum.model.Message;
import podbrushkin.springforum.model.UserDto;
import podbrushkin.springforum.service.UserService;
import podbrushkin.springforum.service.MessageService;
import podbrushkin.springforum.security.SecurityUtilService;



@Controller
@Slf4j
public class MainController {
	
	@Autowired
	UserService userService;
	@Autowired
	MessageService messageService;
	@Autowired
	SecurityUtilService securityUtilService;
	
	@GetMapping("/")
	public ModelAndView root() {
		return new ModelAndView("redirect:/messages");
	}
	
	@GetMapping("/info")
	public ModelAndView userInfo(Authentication authentication) {
		
		String msg = "Hello " + authentication.getName()+", here are your roles: ";
		var sj = new StringJoiner(", ", "", ".");
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			sj.add(authority.getAuthority());
		}
		msg += sj.toString();
		
		var mav = new ModelAndView("basicPage");
		mav.addObject("title", "Info");
		mav.addObject("text", msg);
		return mav;
		
	}
	
	@GetMapping("/createUser")
	public String createUser(Model model) {
		model.addAttribute("user", new UserDto());
		model.addAttribute("possibleRoles", userService.getPossibleRoles());
		return "createUser";
	}
	
	@PostMapping("/createUser")
	public String createUser(Model model, @ModelAttribute("user") @Valid UserDto user, 
		BindingResult bindingResult) {
		model.addAttribute("possibleRoles", userService.getPossibleRoles());	
		if (bindingResult.hasErrors()) return "createUser";
		var freshUser = new UserDto(userService.createUser(user));
		freshUser.setPassword(null);
		model.addAttribute("successMsg", freshUser.toString());
		
		return "createUser";
	}
	
	@GetMapping("/users")
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
	
	@GetMapping("/messages")
	public String messages(Model model) {
		model.addAttribute("message", new Message());
		model.addAttribute("messages", messageService.getAll());
		return "messages";
	}
	
	@PostMapping("/messages")
	public String createMessage(Authentication auth, Model model, 
		@ModelAttribute("message") Message msg) {
		if (msg == null) log.error("Message is null!");
		messageService.createMessage(auth.getPrincipal(), (Message) msg);
		return "redirect:/messages";
	}
	
	@GetMapping("/profile")
	public String profile(Model model) {
		return "profile";
	}
	
	
	@PostMapping("/profile/changeUsername")
	public String changeUsername(Authentication auth,
		@RequestParam(name="newUsername", required=true) String newUsername) {
			
			userService.changeUsernameOfPrincipal(auth.getPrincipal(), newUsername);
			securityUtilService.reauthenticateUser(auth.getPrincipal(), newUsername);
			return "redirect:/";
	}
	
}