package com.onedrinktoday.backend.domain.region.repository;

import com.onedrinktoday.backend.domain.region.entity.Region;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

  Optional<Region> findByPlaceName(String name);

}
