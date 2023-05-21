package com.tasks.store.repository;

import com.tasks.store.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

    Page<Sale> findByItemId(UUID itemId, Pageable pageable);

}
