package com.brevitaz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private JwtAuthFilter jwtAuthFilter;
//
//    @Autowired
//    private JwtAuthenticationProvider jwtAuthenticationProvider;
//
//    @Autowired
//    private JwtAuthenticationEntryPoint jwtAuthEndPoint;
//
//    @Override
//    public void configure(AuthenticationManagerBuilder auth)  throws Exception {
//        auth.authenticationProvider(jwtAuthenticationProvider);
//    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] patterns = new String[] {
                "/v2/api-docs",
                "/public/*",
                "/swagger/*",
                "/api/*"
        };
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers(patterns).permitAll()
                .antMatchers("/**/*")
                .permitAll();
//                .and()
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling()
//                .authenticationEntryPoint(jwtAuthEndPoint)
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}