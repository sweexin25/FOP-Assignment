package service;
import data.dataStorage;
import model.Employee;
import java.util.ArrayList;
public class AuthService {
    private Employee currentUser;

    public boolean login(String id, String password, ArrayList<Employee> employees) {
        for (Employee employee : employees) {
            if (employee.getId().equals(id) && employee.getPassword().equals(password)) {
                this.currentUser = employee;
                return true;
            }
        }
        return false;
    }
    public  Employee getCurrentUser() {
        return currentUser;
    }


}
