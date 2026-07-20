package org.yap.mymarketapp.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.ItemModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemRepository extends R2dbcRepository<ItemModel, Long> {

    @Query("SELECT * FROM item WHERE UPPER(title) LIKE UPPER(CONCAT('%', :keyword, '%')) OR UPPER(description) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    Flux<ItemModel> search(@Param("keyword") String keyword);

    @Query("SELECT COUNT(*) FROM item WHERE UPPER(title) LIKE UPPER(CONCAT('%', :keyword, '%')) OR UPPER(description) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    Mono<Long> countByKeyword(@Param("keyword") String keyword);

    Mono<Long> count();
}
