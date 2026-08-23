package org.yap.mymarketapp.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yap.mymarketapp.model.UserModel;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<UserModel, Long> {
    Mono<UserModel> findByLogin(@Param("login") String login);
}