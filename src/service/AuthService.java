package service;
import data.dataStorage;
import model.Employee;
import java.util.ArrayList;
public class AuthService {
    public Employee currentUser;
    private dataStorage storage;
    public AuthService(dataStorage storage) {
        this.storage = storage;
    }
    public boolean login(String id, String password, ArrayList<Employee> employees) {
        for (Employee employee : employees) {
            if (employee.getId().equals(id) && employee.getPassword().equals(password)) {
                this.currentUser = employee;
                return true;
            }
        }
        return false;
    }
    public void uniqueEmployee(String newID, String newName, String newRole, String newPass,ArrayList<Employee> employees){
        for (Employee employee: employees){
            storage.getEmployees();
            if (employee.getId().equals(newID)){
                System.out.println("Employee ID already exists!");
                return;
            }
            else if (employee.getName().equals(newName)) {
                System.out.println("Employee Name already exists!");
                return;
            }
            else if(employee.getPassword().equals(newPass)){
                System.out.println("Employee Password already exists!");
                return;
            }
            else if (!newRole.equalsIgnoreCase("Part-time") && !newRole.equalsIgnoreCase("Full-time")){
                System.out.println("Invalid role! Please enter 'Part-time' or 'Full-time' only!");
                return;
            }
        }
        storage.registerEmployee(newID, newName, newRole, newPass);
    }
    public void logOut(){
        System.out.println("Logging out....");
        this.currentUser = null;

        System.out.println("Logout successful!");
    }
    public  Employee getCurrentUser() {
        return currentUser;
    }


}
