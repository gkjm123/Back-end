package com.onedrinktoday.backend.domain.announcement.repository;

import com.onedrinktoday.backend.domain.announcement.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

}
