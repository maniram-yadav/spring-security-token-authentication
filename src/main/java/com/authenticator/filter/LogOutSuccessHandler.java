package com.authenticator.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.authenticator.model.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;


public class LogOutSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		ResponseDTO responseDTO=new ResponseDTO();
		responseDTO.setResponseCode(HttpStatus.OK.value());
		responseDTO.setData("User LogOut Successfully");
		response.getOutputStream().write(new ObjectMapper().writeValueAsString(responseDTO).getBytes());
	}

}
