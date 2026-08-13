package com.hotel.packcheck.entity;

import com.hotel.packcheck.enums.CartMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "carts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_hotel",
                        columnNames = {"hotel_id", "cart_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartRecordId;

    @Column(name = "cart_id", nullable = false)
    private String cartId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartMode mode = CartMode.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
}