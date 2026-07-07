package org.yap.mymarketapp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.ItemModel;

@Repository
public interface ItemRepository extends JpaRepository<ItemModel, Long> {

    Page<ItemModel> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameKeyword,
            String descKeyword,
            Pageable pageable
    );
}
