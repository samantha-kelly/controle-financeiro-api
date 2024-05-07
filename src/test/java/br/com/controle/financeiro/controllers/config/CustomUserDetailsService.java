package br.com.controle.financeiro.controllers.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("usuarioTeste".equals(username)) {
            // Usuário com permissões ROLE_ADMIN
            return User.withUsername("usuarioTeste")
                    .password("password")
                    .roles("ADMIN")
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
