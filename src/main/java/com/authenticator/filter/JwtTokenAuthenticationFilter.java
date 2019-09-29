package com.authenticator.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.authenticator.model.LoginRequest;
import com.authenticator.model.ResponseDTO;
import com.authenticator.model.Users;
import com.authenticator.service.UserCrudRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	Logger LOGGER=LoggerFactory.getLogger(JwtTokenAuthenticationFilter.class);

	@Autowired
	private JwtTokenUtility jwtTokenUtility;
	
	@Autowired
	private UserCrudRepository userCrudRepository;
	
	public JwtTokenAuthenticationFilter(String requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		LoginRequest log=null;
		LOGGER.debug("Inside attemptAuthentication ");
		try {
			 log=new ObjectMapper().readValue(request.getInputStream(),LoginRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Exception : ",e);
		} 
		Authentication auth=new UsernamePasswordAuthenticationToken(log.getEmail(), log.getPassword());
		auth= getAuthenticationManager().authenticate(auth);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return auth;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
	    
		Set<SimpleGrantedAuthority> authorities=(Set<SimpleGrantedAuthority>)((UserDetails) authResult.getPrincipal()).getAuthorities();
		String token=jwtTokenUtility.createToken(authResult.getName(), authorities);
		ResponseDTO responseDTO=new ResponseDTO();
		responseDTO.setResponseCode(HttpStatus.OK.value());
		responseDTO.setData("Account logged in successfully");
		response.addHeader("token", "Bearer "+token);
		Users users=userCrudRepository.findByEmail(authResult.getName());
		users.setTokens(token);
		userCrudRepository.save(users);
		response.getOutputStream().write(new ObjectMapper().writeValueAsString(responseDTO).getBytes());
	}
}
