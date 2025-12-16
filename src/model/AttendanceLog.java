package model;

import java.time.LocalTime;
import java.time.LocalDate;

public class AttendanceLog {
    private String employeeId;
    private LocalDate date;
    private LocalTime time;
    private String type; //clock in or out

    //Constructors
    public AttendanceLog(String employeeId, LocalDate date, LocalTime time, String type) {
        this.employeeId = employeeId;
        this.date = date;
        this.time = time;
        this.type = type;
    }

    //Getters
    public String getEmployeeId() {return  employeeId;}
    public LocalDate getDate() {return  date;}
    public LocalTime getTime() {return  time;}
    public String getType() {return  type;}

    public String attendanceToCSV(){
        return employeeId+","+date+","+time+","+type;
    }

}
