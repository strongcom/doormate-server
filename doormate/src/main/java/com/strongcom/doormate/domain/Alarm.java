package com.strongcom.doormate.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name="alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Reminder reminder;

    @Column(nullable = false)
    private LocalDate noticeDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    /**
     * 연관관계 메서드
     */
    public void setReminder(Reminder reminder) {
        if (this.reminder != null) {
            this.reminder.getAlarms().remove(this);
        }
        this.reminder = reminder;
        reminder.getAlarms().add(this);
    }

    @Builder
    public Alarm(Reminder reminder, LocalDate noticeDate, LocalTime startTime, LocalTime endTime) {
        setReminder(reminder);
        this.noticeDate = noticeDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    /**
     * 생성 메서드
     */
    public static Alarm createAlarm(LocalDate date, Reminder reminder) {
        return Alarm.builder()
                .reminder(reminder)
                .noticeDate(date)
                .startTime(reminder.getStartTime())
                .endTime(reminder.getEndTime())
                .build();
    }

    /**
     * 비즈니스 로직
     */
    public void setNoticeDate(LocalDate day){
        this.noticeDate = day;
    }



}
