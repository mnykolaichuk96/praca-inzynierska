package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.security;

import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.dao.EmployeeRepository;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Authority;
import com.mnykolaichuk.luv2code.springboot.thymeleafdemo.model.entity.Employee;
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
public class EmployeeUserDetailService implements UserDetailsService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Employee employee = employeeRepository.findEmployeeByUsername(username);
        if(employee == null) {
            throw new UsernameNotFoundException(username);
        }
        boolean enabled = !employee.getEmployeeDetail().isAccountVerified();
        UserDetails user = User.withUsername(employee.getUsername())
                .password(employee.getPassword())
                .disabled(enabled)
                .authorities(mapRolesToAuthorities(employee.getAuthorities())).build();

        return user;
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Authority> authorities) {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getAuthority().toString())).collect(Collectors.toList());
    }
}
