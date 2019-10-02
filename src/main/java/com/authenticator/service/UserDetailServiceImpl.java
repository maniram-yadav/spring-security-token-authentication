package com.authenticator.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.authenticator.model.InvalidTokens;
import com.authenticator.model.Roles;
import com.authenticator.model.Users;


@Component
public class UserDetailServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserCrudRepository userCrudRepository;

	@Autowired
	private InvalidTokensRepository invalidTokensRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users users=userCrudRepository.findByEmail(username);
		if(users==null) throw new UsernameNotFoundException("User not found");

		if(users.getTokens()!=null){
			InvalidTokens invalidTokens=new InvalidTokens();
			invalidTokens.setToken(users.getTokens());
			invalidTokensRepository.save(invalidTokens);
		}
		return new User(users.getEmail(),users.getPassword(), true, true, true, true, getAUthority(users));
	}
	
	private List<GrantedAuthority> getAUthority(Users user){
		List<GrantedAuthority> authority=new ArrayList<>();
		for(Roles roles:user.getRoles()){
			authority.add(new SimpleGrantedAuthority(roles.getRole()));
		}
		return authority;
	}
	


}
