package com.hotel.packcheck.security;

import com.hotel.packcheck.entity.BellboyHead;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class BellboyHeadUserDetails implements UserDetails {

    private final BellboyHead bellboyHead;

    public BellboyHeadUserDetails(BellboyHead bellboyHead) {
        this.bellboyHead = bellboyHead;
    }

    public BellboyHead getBellboyHead() {
        return bellboyHead;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_BELLBOY_HEAD")
        );
    }

    @Override
    public String getPassword() {
        return bellboyHead.getPassword();
    }

    @Override
    public String getUsername() {
        return bellboyHead.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}