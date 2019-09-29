package com.authenticator.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.authenticator.filter.AccessDeniedException;
import com.authenticator.filter.AnynomusAuthFilter;
import com.authenticator.filter.JwtTokenAuthenticationFilter;
import com.authenticator.filter.JwtTokenAuthorizationFilter;
import com.authenticator.filter.JwtTokenUtility;
import com.authenticator.filter.LogOutHandler;
import com.authenticator.filter.LogOutSuccessHandler;
import com.authenticator.service.InvalidTokensRepository;
import com.authenticator.service.UserDetailServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true,order=2)
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailServiceImpl userService;
	
	@Autowired
	private InvalidTokensRepository invalidTokensRepository;

	@Autowired
	private Environment environment;
	
	@Override
	public UserDetailsService userDetailsServiceBean() throws Exception {
		return super.userDetailsServiceBean(); 
	}
	
	@Bean
	public BCryptPasswordEncoder getEncoder(){
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public JwtTokenUtility getjwtTokenUtility(){
		return new JwtTokenUtility();
	}

	@Bean
	public JwtTokenAuthenticationFilter getJwtTokenAuthenticationFilter() throws Exception{
		JwtTokenAuthenticationFilter auth=new JwtTokenAuthenticationFilter(environment.getProperty("spring.app.login.url"));
		auth.setAuthenticationManager(authenticationManager());	
		return auth;
	}

	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(getEncoder());
	}
	
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Override
	protected UserDetailsService userDetailsService() {
		return this.userService; 
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.headers().frameOptions().disable();


		http.authorizeRequests()
			.antMatchers("/h2-console/**").permitAll()
			.antMatchers(HttpMethod.POST,environment.getProperty("spring.app.login.url")).permitAll()
			.antMatchers(environment.getProperty("spring.app.logout.url")).permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.logout()
			.logoutUrl(environment.getProperty("spring.app.logout.url")).addLogoutHandler(new LogOutHandler(getjwtTokenUtility(),invalidTokensRepository))
			.logoutSuccessHandler(new LogOutSuccessHandler())
			.and()
			.exceptionHandling().accessDeniedHandler(new AccessDeniedException())
			.and()
			.addFilter(new JwtTokenAuthorizationFilter(super.authenticationManager(),getjwtTokenUtility(),invalidTokensRepository))
			.addFilterBefore(getJwtTokenAuthenticationFilter(),UsernamePasswordAuthenticationFilter.class)
			.anonymous().authenticationFilter(new AnynomusAuthFilter(environment.getProperty("spring.app.anynomus.key")));
	}
}
