package model;

public class Employee {
    private String id;
    private String name;
    private String role;
    private String password;

    public Employee(String id, String name, String role, String password) {
        this.id =id;
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
