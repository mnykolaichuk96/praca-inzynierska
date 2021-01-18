package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.security;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.WorkshopRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Authority;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Workshop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

//поки що я розумію що це метод для того щоб спрінг розумів як логувати юзера, що є його юзернеймом
public class WorkshopUserDetailService implements UserDetailsService {

    @Autowired
    WorkshopRepository workshopRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Workshop workshop = workshopRepository.findWorkshopByUsername(username);
        if(workshop == null) {
            throw new UsernameNotFoundException(username);
        }
        boolean enabled = !workshop.isAccountVerified();
        UserDetails user = User.withUsername(workshop.getUsername())
                .password(workshop.getPassword())
                .disabled(enabled)
                .authorities(mapRolesToAuthorities(workshop.getAuthorities())).build();

        return user;
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Authority> authorities) {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getAuthority().toString())).collect(Collectors.toList());
    }
}
