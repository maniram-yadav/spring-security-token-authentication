package com.authenticator.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.authenticator.model.InvalidTokens;
import com.authenticator.service.InvalidTokensRepository;
import com.sun.media.jfxmedia.logging.Logger;

public class JwtTokenAuthorizationFilter extends BasicAuthenticationFilter {

	private JwtTokenUtility jwtTokenUtility;
	private InvalidTokensRepository invalidTokensRepository;

	public JwtTokenAuthorizationFilter(AuthenticationManager authenticationManager, JwtTokenUtility jwtTokenUtility,
			InvalidTokensRepository invalidTokensRepository) {
		super(authenticationManager);
		this.jwtTokenUtility = jwtTokenUtility;
		this.invalidTokensRepository = invalidTokensRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String header = jwtTokenUtility.resolveToken(request);
		if (header == null) {
			chain.doFilter(request, response);
			return;
		}
		UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);
		super.doFilterInternal(request, response, chain);

	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

		boolean isTokenValid = true;
		String token = jwtTokenUtility.resolveToken(request);
		if (token != null) {
			List<InvalidTokens> invalidTokens = (List<InvalidTokens>) invalidTokensRepository.findAll();
			for (InvalidTokens tokens : invalidTokens) {
				try {
					if (token.equals(tokens.getToken())) {
						isTokenValid = false;
					}
					else if (jwtTokenUtility.isExpiredToken(token)) {
						invalidTokensRepository.delete(tokens);
					}
				} catch (Exception ex) {
					invalidTokensRepository.delete(tokens);
				}
			}
			if (isTokenValid) {
				String userName = jwtTokenUtility.getUserName(token);
				List<SimpleGrantedAuthority> authorities = jwtTokenUtility.getAuthority(token);
				return new UsernamePasswordAuthenticationToken(userName, null, authorities);
			}
		}
		return null;

	}
}
