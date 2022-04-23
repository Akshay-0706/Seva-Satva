package com.official.sevasatva;

public class getDateNTime {

    public static String getMonth(int month) {
        String monthString = "";
        switch (month) {
            case 1:
                monthString = "January";
                break;
            case 2:
                monthString = "February";
                break;
            case 3:
                monthString = "March";
                break;
            case 4:
                monthString = "April";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "June";
                break;
            case 7:
                monthString = "July";
                break;
            case 8:
                monthString = "August";
                break;
            case 9:
                monthString = "September";
                break;
            case 10:
                monthString = "October";
                break;
            case 11:
                monthString = "November";
                break;
            case 12:
                monthString = "December";
                break;
            default:
                monthString = "Invalid month";
                break;
        }
        return monthString;
    }

    public static String getTime(String time, int seconds, boolean withSeconds) {

        int hour = Integer.parseInt(time.substring(0, 2));
        String minutes = time.substring(3, 5);

        String meridian;

        if (hour < 12) {
            meridian = "am";
        } else
            meridian = "pm";

        hour %= 12;

        if (hour == 0)
            hour = 12;

        if (withSeconds)
            if (seconds < 10)
                return hour + ":" + minutes + ":0" + seconds + " " + meridian;
            else
                return hour + ":" + minutes + ":" + seconds + " " + meridian;
        else
            return hour + ":" + minutes + " " + meridian;

    }
}
