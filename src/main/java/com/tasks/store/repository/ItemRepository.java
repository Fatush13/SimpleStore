package com.tasks.store.repository;

import com.tasks.store.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {

    Optional<Item> findById(UUID itemId);

    Page<Item> findAll(Pageable pageable);

    void deleteById(UUID itemId);

    boolean existsById(UUID itemId);

}
