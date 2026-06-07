package com.ruralhealth.config;

import com.ruralhealth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
        var user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + mobile));

        return new org.springframework.security.core.userdetails.User(
                user.getMobile(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
