package com.strongcom.doormate.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RepetitionDayTest {

    @Test
    void toDay() {
        String s = "MON TUE WED";
        String day = RepetitionDay.toDay(s);
        System.out.println("day = " + day);
    }
}