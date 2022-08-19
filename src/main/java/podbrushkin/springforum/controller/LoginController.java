package podbrushkin.springforum.controller;

import podbrushkin.springforum.security.SecurityUtilService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LoginController {
	
	@Autowired
	SecurityUtilService securityUtilService;
	
	@GetMapping("/login")
	String login(Model model) {
		model.addAttribute("loginGoogle", "oauth2/authorization/google");
		return "login";
	}
	
	@GetMapping("/oauth_success")
	String oauthSuccess(Authentication auth) {
		securityUtilService.reauthenticateOidcUser(auth.getPrincipal());
		return "redirect:/";
	}
	
}