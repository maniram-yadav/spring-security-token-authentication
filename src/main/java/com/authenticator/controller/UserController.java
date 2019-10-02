package com.authenticator.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authenticator.model.Users;
import com.authenticator.service.UserCrudRepository;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserCrudRepository userCrudRepository;
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
		
	@GetMapping(path="/")
	public ResponseEntity<List<Users>> finAllUser(){
		List<Users> allUsers=(List<Users>) userCrudRepository.findAll();
		return new ResponseEntity<List<Users>>(allUsers,HttpStatus.OK);
	}

	@GetMapping(path="/{id}")
	public ResponseEntity<Users> finUserById(@PathVariable("id") Integer id){
		Users users=userCrudRepository.findByUserId(id);
		return new ResponseEntity<Users>(users,HttpStatus.OK);
	}

	@PutMapping("/updatepassword/{id}")
	public ResponseEntity<Users> updateUser(@PathVariable("id") Integer id,@NotNull @RequestBody Users users){
		Users user=userCrudRepository.findByUserId(id);
		if(user==null){
			throw new RuntimeException("User not exists");
		}
		if(users.getPassword()==""||users.getPassword()==null){
			throw new RuntimeException("Invalid password");
		}
		user.setPassword(bcryptPasswordEncoder.encode(users.getPassword()));
		userCrudRepository.save(user);
		return new ResponseEntity<Users>(user,HttpStatus.OK);
	}
}
