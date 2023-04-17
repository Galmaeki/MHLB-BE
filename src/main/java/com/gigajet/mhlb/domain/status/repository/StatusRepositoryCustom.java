package com.gigajet.mhlb.domain.status.repository;

import com.gigajet.mhlb.domain.status.entity.Status;
import com.gigajet.mhlb.domain.status.entity.StatusEnum;
import com.gigajet.mhlb.domain.user.entity.User;

public interface StatusRepositoryCustom {
    Status findStatusByUser(User user);

    StatusEnum findStatusEnumByUser(User user);
}
