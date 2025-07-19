package io.github.pavelshe11.networkingmicro.store.repositories;

import io.github.pavelshe11.networkingmicro.store.entities.PortfolioItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItemEntity, UUID> {
}
