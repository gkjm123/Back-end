package com.onedrinktoday.backend.domain.drink.repository;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import com.onedrinktoday.backend.domain.region.entity.Region;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long> {

  List<Drink> findByRegion(Region region);

  Page<Drink> findAllByRegion_IdAndNameContaining(Pageable pageable, Long regionId, String name);

  Page<Drink> findAllByNameContaining(Pageable pageable, String name);

  List<Drink> findAllByRegion_IdAndNameStartsWith(Long regionId, String name);
}
