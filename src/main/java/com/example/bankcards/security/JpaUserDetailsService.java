package com.example.bankcards.security;

import lombok.RequiredArgsConstructor;
import com.example.bankcards.entity.UserAccount;
import com.example.bankcards.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {
    private final UserAccountRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount ua = users.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));


        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + ua.getRole().name()));

        return User.withUsername(ua.getUsername())
                .password(ua.getPasswordHash())
                .authorities(authorities)
                .accountLocked(false)
                .disabled(false)
                .build();
    }
}
