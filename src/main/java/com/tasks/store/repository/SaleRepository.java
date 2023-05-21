package com.tasks.store.repository;

import com.tasks.store.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

    @Query("SELECT SUM(s.quantitySold) FROM Sale s WHERE s.item.id = :itemId")
    Integer findTotalSoldByItemId(@Param("itemId") UUID itemId);

    Page<Sale> findByItemId(UUID itemId, Pageable pageable);

}
