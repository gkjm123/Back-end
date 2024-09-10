package com.onedrinktoday.backend.domain.registration.repository;

import com.onedrinktoday.backend.domain.registration.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

}
