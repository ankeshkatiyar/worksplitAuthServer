package com.worksplit.auth.server.userconfig;

import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User extends BaseId implements UserDetails {

    @Size(min = 4 , max = 30)
    private String username;


    @Size(min = 8 , max = 20)
    private String password;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Column(name = "account_expired")
    private boolean accountNotExpired;

    @Column(name = "credentials_expired")
    private boolean credentialsNotExpired;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "account_locked")
    private  boolean accountNotLocked;

    @ManyToMany(fetch = FetchType.EAGER)
            @JoinTable(name = "role_user" , joinColumns = {
                    @JoinColumn(name = "user_id" , referencedColumnName = "id")} , inverseJoinColumns = {
                    @JoinColumn(name = "role_id" , referencedColumnName = "id")
            })
    List<Role> roles;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            role.getPermissions().forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            });
        });
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountNotExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountNotLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsNotExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getEmail() {
        return email;
    }

    public List<Role> getRoles() {
        return roles;
    }
}
