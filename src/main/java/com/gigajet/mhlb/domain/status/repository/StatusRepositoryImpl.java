package com.gigajet.mhlb.domain.status.repository;

import com.gigajet.mhlb.domain.status.entity.Status;
import com.gigajet.mhlb.domain.status.entity.StatusEnum;
import com.gigajet.mhlb.domain.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.gigajet.mhlb.domain.status.entity.QStatus.status1;

@RequiredArgsConstructor
public class StatusRepositoryImpl implements StatusRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Status findStatusByUser(User user) {
        return queryFactory
                .selectFrom(status1)
                .where(status1.user.eq(user))
                .orderBy(status1.updateDay.desc(), status1.updateTime.desc())
                .fetchFirst();
    }

    @Override
    public StatusEnum findStatusEnumByUser(User user) {
        return queryFactory
                .select(status1.status)
                .from(status1)
                .where(status1.user.eq(user))
                .orderBy(status1.updateDay.desc(), status1.updateTime.desc())
                .fetchFirst();
    }
}
