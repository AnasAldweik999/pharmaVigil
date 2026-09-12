package com.pharm.pharmavigil_platform.repository.listing;

import com.pharm.pharmavigil_platform.repository.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserListing {
    Page<UserEntity> findAll(Specification<UserEntity> spec, Pageable pageable);
}
