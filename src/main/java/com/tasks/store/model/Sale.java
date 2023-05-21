package com.tasks.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "sale")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="item_id", nullable=false)
    private Item item;

    @Column(nullable = false)
    private Integer quantitySold;

}
