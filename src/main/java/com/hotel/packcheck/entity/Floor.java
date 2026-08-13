package com.hotel.packcheck.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "floors",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_floor_hotel",
                        columnNames = {"hotel_id", "floor_number"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long floorId;

    @Column(name = "floor_number", nullable = false)
    private int floorNumber;

    @Column(name = "bssid_1")
    private String bssid1;

    @Column(name = "bssid_2")
    private String bssid2;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
}