package com.gigajet.mhlb.domain.workspace.repository;

import com.gigajet.mhlb.domain.workspace.entity.WorkspaceInvite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceInviteRepository extends JpaRepository<WorkspaceInvite, Long>, WorkspaceInviteRepositoryCustom {
    int countByEmail(String email);
}
