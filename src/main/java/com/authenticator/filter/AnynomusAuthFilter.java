package com.authenticator.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import com.authenticator.model.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnynomusAuthFilter extends AnonymousAuthenticationFilter {
	
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		if(SecurityContextHolder.getContext().getAuthentication()==null&&
				!request.getRequestURI().contains("h2-console")){
			ResponseDTO responseDTO=new ResponseDTO();
			responseDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
			responseDTO.setData("Anynomus user authentication failed");
			res.getOutputStream().write(new ObjectMapper().writeValueAsString(responseDTO).getBytes());
		}else{
			chain.doFilter(req, res);
		}
		
	}

	public AnynomusAuthFilter(String key) {
		super(key);
	}

}
