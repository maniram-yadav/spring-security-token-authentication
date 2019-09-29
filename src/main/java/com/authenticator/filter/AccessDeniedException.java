package com.authenticator.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.authenticator.model.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AccessDeniedException implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.access.AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
			ResponseDTO responseDTO=new ResponseDTO();
			responseDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
			responseDTO.setErrorMessage(accessDeniedException.getMessage());
			response.getOutputStream().write(new ObjectMapper().writeValueAsString(responseDTO).getBytes());
	}

}
