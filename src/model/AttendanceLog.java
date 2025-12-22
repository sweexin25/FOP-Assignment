package model;

import java.time.LocalTime;
import java.time.LocalDate;

public class AttendanceLog {
    private String employeeId;
    private LocalDate date;
    private LocalTime time;
    private String type; //clock in or out
    private String outlet;

    //Constructors
    public AttendanceLog(String employeeId, LocalDate date, LocalTime time, String type,String outlet) {
        this.employeeId = employeeId;
        this.date = date;
        this.time = time;
        this.type = type;
        this.outlet = outlet;
    }

    //Getters
    public String getEmployeeId() {return  employeeId;}
    public LocalDate getDate() {return  date;}
    public LocalTime getTime() {return  time;}
    public String getType() {return  type;}
    public String getOutlet() {return  outlet;}

    public String attendanceToCSV(){
        return employeeId+","+date+","+time+","+type+","+outlet;
    }

}
