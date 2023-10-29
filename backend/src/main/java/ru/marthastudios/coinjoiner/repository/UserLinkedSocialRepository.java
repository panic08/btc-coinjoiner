package ru.marthastudios.coinjoiner.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.marthastudios.coinjoiner.enums.UserLinkedSocialType;
import ru.marthastudios.coinjoiner.model.UserLinkedSocial;

@Repository
public interface UserLinkedSocialRepository extends ReactiveCrudRepository<UserLinkedSocial, Long> {
    @Query("SELECT uls.* FROM user_linked_socials_table uls WHERE uls.identifier = :identifier AND uls.type = :type")
    Mono<UserLinkedSocial> findByIdentifierAndType(@Param("identifier") String identifier,
                                            @Param("type") UserLinkedSocialType type);
}
