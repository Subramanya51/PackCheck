package com.hotel.packcheck.security;

import com.hotel.packcheck.entity.BellboyHead;
import com.hotel.packcheck.repository.BellboyHeadRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class BellboyHeadUserDetailsService
        implements UserDetailsService {

    private final BellboyHeadRepository bellboyHeadRepository;

    public BellboyHeadUserDetailsService(
            BellboyHeadRepository bellboyHeadRepository) {

        this.bellboyHeadRepository = bellboyHeadRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId)
            throws UsernameNotFoundException {

        BellboyHead bellboyHead =
                bellboyHeadRepository.findByLoginId(loginId)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Bellboy Head not found."
                                ));

        return new BellboyHeadUserDetails(bellboyHead);
    }
}