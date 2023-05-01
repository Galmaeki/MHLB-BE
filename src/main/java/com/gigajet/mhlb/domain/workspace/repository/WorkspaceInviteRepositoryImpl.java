package com.gigajet.mhlb.domain.workspace.repository;

import com.gigajet.mhlb.domain.mypage.dto.MypageResponseDto;
import com.gigajet.mhlb.domain.workspace.dto.WorkspaceResponseDto;
import com.gigajet.mhlb.domain.workspace.entity.Workspace;
import com.gigajet.mhlb.domain.workspace.entity.WorkspaceInvite;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.gigajet.mhlb.domain.workspace.entity.QWorkspaceInvite.workspaceInvite;

@RequiredArgsConstructor
public class WorkspaceInviteRepositoryImpl implements WorkspaceInviteRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<WorkspaceResponseDto.Invite> findInviteListByWorkspace(Workspace workspace) {
        return queryFactory
                .select(Projections.constructor(WorkspaceResponseDto.Invite.class,
                        workspaceInvite.id,
                        workspaceInvite.email))
                .from(workspaceInvite)
                .where(workspaceInvite.workspace.eq(workspace))
                .orderBy(workspaceInvite.id.desc())
                .fetch();
    }

    @Override
    public Optional<WorkspaceInvite> findInviteByUserIdAndId(Long workspaceId, Long id) {
        WorkspaceInvite invite = queryFactory
                .select(workspaceInvite)
                .from(workspaceInvite)
                .where(workspaceInvite.workspace.id.eq(workspaceId),
                        workspaceInvite.id.eq(id))
                .fetchFirst();

        return Optional.ofNullable(invite);
    }

    @Override
    public Optional<WorkspaceInvite> findOptionalByEmailAndWorkspace(String email, Workspace workspace) {
        WorkspaceInvite invite = queryFactory
                .selectFrom(workspaceInvite)
                .where(workspaceInvite.email.eq(email).and(
                        workspaceInvite.workspace.eq(workspace)))
                .fetchFirst();

        return Optional.ofNullable(invite);
    }

    @Override
    public List<MypageResponseDto.InviteList> findMyPageListByEmail(String email) {
        return queryFactory
                .select(Projections.constructor(MypageResponseDto.InviteList.class,
                        workspaceInvite.workspace.id,
                        workspaceInvite.workspace.image,
                        workspaceInvite.workspace.title,
                        workspaceInvite.workspace.description
                ))
                .from(workspaceInvite)
                .where(workspaceInvite.email.eq(email))
                .fetch();
    }

    @Override
    public void deleteQueryByEmailAndWorkspace(String email, Workspace workspace) {
        queryFactory
                .delete(workspaceInvite)
                .where(workspaceInvite.email.eq(email).and(
                        workspaceInvite.workspace.eq(workspace)))
                .execute();
    }

    @Override
    public void deleteQueryByWorkspace(Workspace workspace) {
        queryFactory
                .delete(workspaceInvite)
                .where(workspaceInvite.workspace.eq(workspace))
                .execute();
    }
}
