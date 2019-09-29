package com.authenticator.filter;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public class CredentialNotFoundException extends AuthenticationCredentialsNotFoundException {

	public CredentialNotFoundException(String msg) {
		super(msg);
	}

}
