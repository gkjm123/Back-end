package com.onedrinktoday.backend.domain.notification.repository;

import com.onedrinktoday.backend.domain.notification.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  List<Notification> findTop20ByMemberIdOrderByCreatedAtDesc(Long member);
}