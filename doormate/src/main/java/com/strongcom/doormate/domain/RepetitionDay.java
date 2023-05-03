package com.strongcom.doormate.domain;

public enum RepetitionDay {
    MON(1, "MON", "월"),
    TUE(2, "TUE", "화"),
    WED(3, "WED", "수"),
    THUR(4, "THUR", "목"),
    FRI(5, "FRI", "금"),
    SAT(6, "SAT", "토"),
    SUN(7, "SUN", "일");

    private int number;
    private String day;
    private String kor_day;

    RepetitionDay(int number, String day, String kor_day) {
        this.number = number;
        this.day = day;
        this.kor_day = kor_day;
    }

    public int getNumber() {
        return number;
    }

    public String getDay() {
        return day;
    }

    public String getKor_day(){ return kor_day; }

    public static String toDay(String days) {
        String[] split = days.split(" ");
        String result = "";
        for (String day : split
        ) {
            if (!result.equals("")) result += ", ";
            RepetitionDay repetitionDay = RepetitionDay.valueOf(day);
            String korDay = repetitionDay.getKor_day();
            result += korDay;
        }
        return result;
    }
}
