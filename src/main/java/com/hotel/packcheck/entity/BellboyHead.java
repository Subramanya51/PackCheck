package com.hotel.packcheck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bellboy_heads")
@Getter
@Setter
@NoArgsConstructor
public class BellboyHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bellboyHeadId;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
}