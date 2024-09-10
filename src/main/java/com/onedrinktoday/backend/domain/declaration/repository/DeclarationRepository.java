package com.onedrinktoday.backend.domain.declaration.repository;

import com.onedrinktoday.backend.domain.declaration.entity.Declaration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeclarationRepository extends JpaRepository<Declaration, Long> {

}
