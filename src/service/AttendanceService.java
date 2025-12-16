package service;
import data.dataStorage;
import model.AttendanceLog;
import model.Employee;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AttendanceService {
    private dataStorage storage;
    private LocalTime tempInTime = null;

    //constructors
    public AttendanceService(dataStorage storage) {
        this.storage = storage;
    }

    public void clockIn(Employee user){
        if (tempInTime != null){
            System.out.println("You had clocked in since "+ tempInTime.format(DateTimeFormatter.ofPattern("hh:mm a")));
            return;
        }

        //built in java method for time
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        //store for memory
        this.tempInTime = now; //in this method only

        System.out.println("===== Attendance Clock In =====");
        System.out.println("Employee ID: "+  user.getId());
        System.out.println("Name: "+  user.getName());
        System.out.println("Outlet: ");
        System.out.println();
        System.out.println("Clock in Successful!");
        System.out.println("Date: " + today);
        System.out.println("Time: " + now.format(DateTimeFormatter.ofPattern("hh:mm a")));

        storage.saveAttendance(new AttendanceLog(user.getId(), today,now,"IN"));
    }
    public void clockOut(Employee user){
        if (tempInTime == null){
            System.out.println("You haven't clocked in yet");
            return;
        }
        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();

        System.out.println("===== Attendance Clock Out =====");
        System.out.println("Employee ID: "+ user.getId());
        System.out.println("Name: "+  user.getName());
        System.out.println("Outlet: ");
        System.out.println();
        System.out.println("Clock Out Successful!");
        System.out.println("Date: " + today);
        System.out.println("Time: " + now.format(DateTimeFormatter.ofPattern("hh:mm a")));
        //calculate work hours
        long minutes = Duration.between(tempInTime, now).toMinutes();
        double hours = minutes/60;
        System.out.printf("Total worked hour(s): %d hours %d minutes\n",Math.round(hours), Math.round(minutes%60));

        storage.saveAttendance(new AttendanceLog(user.getId(), today,now,"OUT"));
    }

}
