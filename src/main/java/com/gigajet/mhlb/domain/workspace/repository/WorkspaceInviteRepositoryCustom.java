package com.gigajet.mhlb.domain.workspace.repository;

import com.gigajet.mhlb.domain.mypage.dto.MypageResponseDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceInvite;

import java.util.List;
import java.util.Optional;

public interface WorkspaceInviteRepositoryCustom {
    List<WorkspaceResponseDto.Invite> findInviteListByWorkspace(Workspace workspace);

    Optional<WorkspaceInvite> findInviteByUserIdAndId(Long userId, Long id);

    Optional<WorkspaceInvite> findOptionalByEmailAndWorkspace(String email, Workspace workspace);

    List<MypageResponseDto.InviteList> findMyPageListByEmail(String email);

    void deleteQueryByEmailAndWorkspace(String email, Workspace workspace);

    void deleteQueryByWorkspace(Workspace workspace);
}
