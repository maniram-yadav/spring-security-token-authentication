package com.authenticator.filter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.authenticator.model.InvalidTokens;
import com.authenticator.service.InvalidTokensRepository;

public class LogOutHandler implements LogoutHandler {

	
	private InvalidTokensRepository invalidTokensRepository;
	private JwtTokenUtility jwtTokenUtility;
	
	public  LogOutHandler(JwtTokenUtility jwtTokenUtility,InvalidTokensRepository invalidTokensRepository) {
		this.jwtTokenUtility=jwtTokenUtility;
		this.invalidTokensRepository=invalidTokensRepository;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		SecurityContextHolder.clearContext();

		String token=jwtTokenUtility.resolveToken(request);
		if (token != null) {
			InvalidTokens invalidToken = new InvalidTokens();
			invalidToken.setToken(token);
			invalidTokensRepository.save(invalidToken);
			List<InvalidTokens> invalidTokens = (List<InvalidTokens>) invalidTokensRepository.findAll();
			for (InvalidTokens tokens : invalidTokens) {
				try {
					if (jwtTokenUtility.isExpiredToken(token)) {
						invalidTokensRepository.delete(tokens);
					}
				} catch (Exception ex) {
					invalidTokensRepository.delete(tokens);
				}
			}
		}
	}

}
