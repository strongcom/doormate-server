package com.strongcom.doormate.domain;

import com.strongcom.doormate.dto.ReminderDto;
import com.strongcom.doormate.dto.ReminderPageRespDto;
import com.strongcom.doormate.dto.ReminderRespDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "reminder")
@NoArgsConstructor
@Getter
@Setter
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    private String subTitle;

    private String content;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepetitionPeriod repetitionPeriod;

    private String repetitionDay;

    @OneToMany(mappedBy = "reminder", cascade = CascadeType.ALL, orphanRemoval = true)  // 고아객체가 되었을 경우, 자식 엔티티 자동 삭제
    private List<Alarm> alarms = new ArrayList<>();

    /**
     * 연관관계 메서드
     */
    public void setUser(User user) {
        if (this.user != null) {
            this.user.getReminders().remove(this);
        }
        this.user = user;
        user.getReminders().add(this);
    }

    public void addAlarm(Alarm alarm) {
        this.getAlarms().add(alarm);
        alarm.setReminder(this);
    }

    public void cleanAlarm() {
        this.getAlarms().clear();
    }

    public void clean() {
        if (this.getAlarms().size() == 0) {
            this.getAlarms().clear();
        }
    }



    @Builder
    public Reminder(User user, String title, String content,
                    LocalTime startTime, LocalTime endTime,
                    LocalDate startDate, LocalDate endDate,
                    RepetitionPeriod repetitionPeriod, String repetitionDay) {
        setUser(user);
        addSubtitle(repetitionDay, repetitionPeriod, startTime, endTime);
        this.title = title;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.repetitionPeriod = repetitionPeriod;
        this.repetitionDay = repetitionDay;
    }


    /**
     * 생성 메서드
     */
    public static Reminder createReminder(User user, ReminderDto reminderDto){
        return Reminder.builder()
                .user(user)
                .title(reminderDto.getTitle())
                .content(reminderDto.getContent())
                .startTime(reminderDto.getStartTime())
                .endTime(reminderDto.getEndTime())
                .startDate(reminderDto.getStartDate())
                .endDate(reminderDto.getEndDate())
                .repetitionPeriod(reminderDto.getRepetitionPeriod())
                .repetitionDay(reminderDto.getRepetitionDay())
                .build();
    }

    public static Reminder repetitionReminder(LocalDate localDate, Reminder reminder) {
        return Reminder.builder()
                .user(reminder.getUser())
                .title(reminder.getTitle())
                .content(reminder.getContent())
                .startTime(reminder.getStartTime())
                .endTime(reminder.getEndTime())
                .startDate(localDate)
                .endDate(localDate)
                .repetitionPeriod(reminder.getRepetitionPeriod())
                .repetitionDay(reminder.getRepetitionDay())
                .build();
    }


    /**
     * 비즈니스 로직
     */



    public ReminderRespDto setReminderRespDto() {
        return ReminderRespDto.builder()
                .reminderId(this.reminderId)
                .title(this.title)
                .content(this.content)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .repetitionDay(this.repetitionDay)
                .repetitionPeriod(this.repetitionPeriod)
                .build();
    }

    public void setReminder(ReminderDto reminderDto) {
        setUser(this.user);
        addSubtitle(reminderDto.getRepetitionDay(), reminderDto.getRepetitionPeriod(),
                reminderDto.getStartTime(), reminderDto.getEndTime());
        this.title = reminderDto.getTitle();
        this.content = reminderDto.getContent();
        this.startTime = reminderDto.getStartTime();
        this.endTime = reminderDto.getEndTime();
        this.startDate = reminderDto.getStartDate();
        this.endDate = reminderDto.getEndDate();
        this.repetitionPeriod = reminderDto.getRepetitionPeriod();
        this.repetitionDay = reminderDto.getRepetitionDay();
    }

    public ReminderPageRespDto setReminderPageRespDto() {
        return ReminderPageRespDto.builder()
                .reminderId(this.reminderId)
                .title(this.title)
                .subTitle(this.subTitle)
                .build();
    }

    // 리마인더 날짜 리스트를 만들어야함 (매일, 매주, 매달, 매년)
    public void addSubtitle(String repetitionDay, RepetitionPeriod repetitionPeriod, LocalTime startTime, LocalTime endTime) {
        String repetition = "알람, ";

        if (repetitionPeriod == RepetitionPeriod.DAILY) repetition += "매일" + " " + startTime + " - " + endTime + " 사이 알림";
        if (repetitionPeriod == RepetitionPeriod.WEEKLY)
            repetition += "매주 " + RepetitionDay.toDay(repetitionDay) + " " + startTime + " - " + endTime + " 사이 알림";
        else if (repetitionPeriod == RepetitionPeriod.MONTHLY) repetition += "매달" + " " + startTime + " - " + endTime + " 사이 알림";
        else if (repetitionPeriod == RepetitionPeriod.YEARLY) repetition += "매년" + " " + startTime + " - " + endTime + " 사이 알림";
        else if (repetitionPeriod == RepetitionPeriod.BASIC) repetition += "일회성 알림";

        this.subTitle = repetition;
    }

    public List<LocalDate> findByDate() {
        List<LocalDate> repetitionDates = new ArrayList<>();
        if (this.repetitionPeriod == RepetitionPeriod.BASIC)
            repetitionDates = findDailyRepetition();
        else if (this.repetitionPeriod == RepetitionPeriod.DAILY)
            repetitionDates = findDailyRepetition();
        else if (this.repetitionPeriod == RepetitionPeriod.WEEKLY)
            repetitionDates = findWeeklyRepetition();
        else if (this.repetitionPeriod == RepetitionPeriod.MONTHLY)
            repetitionDates = findMonthlyRepetition();
        else if (this.repetitionPeriod == RepetitionPeriod.YEARLY)
            repetitionDates = findYearlyRepetition();
        return repetitionDates;
    }

    public List<LocalDate> findYearlyRepetition() {
        List<LocalDate> dates = new ArrayList<>();
        List<LocalDate> betweenDate = findAllDate(this.startDate, this.endDate);
        int month = this.startDate.getMonthValue();
        int day = this.startDate.getDayOfMonth();

        for (LocalDate date : betweenDate) {
            if (date.getMonthValue() == month && date.getDayOfMonth() == day) dates.add(date);
        }
        return dates;
    }

    public List<LocalDate> findMonthlyRepetition() {
        List<LocalDate> dates = new ArrayList<>();
        List<LocalDate> betweenDate = findAllDate(this.startDate, this.endDate);
        int month = this.startDate.getDayOfMonth();

        for (LocalDate day : betweenDate)
            if (day.getDayOfMonth() == month) dates.add(day);

        return dates;
    }

    public List<LocalDate> findWeeklyRepetition() {
        List<LocalDate> dates = new ArrayList<>();
        List<LocalDate> betweenDate = findAllDate(this.startDate, this.endDate);
        ArrayList<Integer> repetitionDate = findByRepetitionDate(this.repetitionDay);

        for (LocalDate day : betweenDate
        ) {
            int day_num = day.getDayOfWeek().getValue();
            if (repetitionDate.contains(day_num)) dates.add(day);
        }
        return dates;
    }

    public List<LocalDate> findDailyRepetition() {
        List<LocalDate> dates = new ArrayList<>();
        List<LocalDate> betweenDate = findAllDate(this.startDate, this.endDate);
        for (LocalDate day : betweenDate) dates.add(day);
        return dates;
    }


    // startDate - endDate 사이의 모든 날짜 추출
    public List<LocalDate> findAllDate(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toList());
    }

    // 빈복 요일 추출(숫자형태로 추출)
    public ArrayList<Integer> findByRepetitionDate(String repetitionDay) {
        List<String> repetitionDayList = Arrays.asList(repetitionDay.split(" "));
        ArrayList<Integer> repetitionNumberList = new ArrayList<>();

        for (RepetitionDay day : RepetitionDay.values())
            if (repetitionDayList.contains(day.getDay())) repetitionNumberList.add(day.getNumber());

        return repetitionNumberList;
    }
}
