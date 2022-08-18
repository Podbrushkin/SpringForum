package podbrushkin.springforum.controller;

import podbrushkin.springforum.service.UserService;
import podbrushkin.springforum.security.MyUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.HashMap;

@Controller
@Slf4j
public class LoginController {
	
	@Autowired
	UserService userService;
	
	@GetMapping("/login")
	String loginClassic(Model model) {
		return "login";
	}
	
	private static String authorizationRequestBaseUri = "oauth2/authorization";
    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
	
	@GetMapping("/oauth_login")
	String loginOauth(Model model) {
		Iterable<ClientRegistration> clientRegistrations = null;
		ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
			.as(Iterable.class);
		if (type != ResolvableType.NONE && 
			ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
				clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
		}

		clientRegistrations.forEach(registration -> 
			oauth2AuthenticationUrls.put(registration.getClientName(), 
			authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
		model.addAttribute("urls", oauth2AuthenticationUrls);

		return "loginOauth";
	}
	
	@GetMapping("/oauth_success")
	String oauthSuccess(Authentication auth) {
		if (auth.getPrincipal() instanceof DefaultOidcUser) {
			var user = userService.createUserFromOid(auth.getPrincipal());
			var newAuth = new UsernamePasswordAuthenticationToken(new MyUserDetails(user), null, 
				user.getRoles().stream()
					.map(s -> new SimpleGrantedAuthority(s.strip())).toList());
			SecurityContextHolder.getContext().setAuthentication(newAuth);
		}
		return "redirect:/";
	}
	
}