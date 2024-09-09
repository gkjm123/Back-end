package com.onedrinktoday.backend.domain.drink.repository;

import com.onedrinktoday.backend.domain.drink.entity.Drink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrinkRepository extends JpaRepository<Drink, Long> {
}
