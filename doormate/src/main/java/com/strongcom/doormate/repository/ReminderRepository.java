package com.strongcom.doormate.repository;

import com.strongcom.doormate.domain.Reminder;
import com.strongcom.doormate.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findAllByUser(User user);
}
