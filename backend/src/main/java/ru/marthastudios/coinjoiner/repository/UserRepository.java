package ru.marthastudios.coinjoiner.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.marthastudios.coinjoiner.model.User;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
}
