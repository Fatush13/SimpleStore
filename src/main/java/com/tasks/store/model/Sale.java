package com.tasks.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private Long quantitySold;

}
