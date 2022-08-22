package podbrushkin.springforum.security;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/**
* The only purpose of this copy-pasted class is to add
* "prompt=consent" parameter when sending oauth2 request to Google
* so Google will always show it's consent screen.
* To enable, add this to SecurityConfiguration.configure():
* {@code
* .authorizationEndpoint()
*                   .authorizationRequestResolver(
*                           new CustomAuthorizationRequestResolver(
*                                   this.clientRegistrationRepository()));
* }
* @see <a href="https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/htmlsingle/#oauth2Client-authorization-request-resolver">Spring Documentation</a>
* @see <a href="https://www.baeldung.com/spring-security-custom-oauth-requests">Baeldung</a>
*
*/
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    
    private OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo, String authorizationRequestBaseUri){
        defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri);
    }
	public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo){
		defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        if(req != null){
            req = customizeAuthorizationRequest(req);
        }
        return req;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
        if(req != null){
            req = customizeAuthorizationRequest(req);
        }
        return req;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest req) {
        Map<String,Object> extraParams = new HashMap<String,Object>();
        extraParams.putAll(req.getAdditionalParameters());
        extraParams.put("prompt", "consent");
        return OAuth2AuthorizationRequest.from(req).additionalParameters(extraParams).build();
    }
	
}