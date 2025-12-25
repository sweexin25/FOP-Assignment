package model;

public class Employee {
    private String id;
    private String name;
    private String role;
    private String password;

    public Employee(String id, String name, String role, String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    // These methods let other parts of the app read the data
    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    // These methods allow us to change the data
    // We need these for the edit feature to work
    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}