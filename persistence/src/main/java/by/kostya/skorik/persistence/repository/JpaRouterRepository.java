package by.kostya.skorik.persistence.repository;

import by.kostya.skorik.persistence.entity.RouterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface JpaRouterRepository extends JpaRepository<RouterEntity, Long> {
    Optional<RouterEntity> findByIp(String ip);
}
