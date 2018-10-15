package com.helioscloud.atlantis;

import com.helioscloud.atlantis.security.PrincipalLite;
import com.helioscloud.atlantis.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
@AllArgsConstructor
public class OAuthHelperH2 {
	ClientDetailsService clientDetailsService;

	// For use with @WithOAuth2Authentication
	public OAuth2Authentication oAuth2Authentication(final String clientId, final String username) {
		// Look up authorities, resourceIds and scopes based on clientId
		ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
		Collection<GrantedAuthority> authorities = client.getAuthorities();
		Set<String> resourceIds = client.getResourceIds();
		Set<String> scopes = client.getScope();

		// Default values for other parameters
		Map<String, String> requestParameters = Collections.emptyMap();
		boolean approved = true;
		String redirectUrl = null;
		Set<String> responseTypes = Collections.emptySet();
		Map<String, Serializable> extensionProperties = Collections.emptyMap();

		// Create request
		OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, clientId, authorities, approved, scopes, resourceIds, redirectUrl, responseTypes, extensionProperties);

		// Create OAuth2AccessToken
		UserDetails user = null;
		if ("13323454321".equals(username)) {
			UserDTO userDTO = new UserDTO();
			userDTO.setAvatar("123");
			userDTO.setAuthorities(new HashSet<>());
			user = new PrincipalLite(userDTO);
		}
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);
		OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);
		return auth;
	}
}
