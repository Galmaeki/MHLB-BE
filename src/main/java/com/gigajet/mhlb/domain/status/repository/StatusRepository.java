package com.gigajet.mhlb.domain.status.repository;

import com.gigajet.mhlb.domain.status.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long>, StatusRepositoryCustom {
}
