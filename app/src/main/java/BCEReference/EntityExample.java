package BCEReference;

import com.example.dietandnutritionapplication.Profile;
import com.example.dietandnutritionapplication.User;

import java.util.ArrayList;

public class EntityExample {
    public ArrayList<Profile> accountArray = new ArrayList<>();
    public EntityExample(){

    }
    public void createUserAccount(String firstname, String username, String dob, String email, String phNum, String gender, String password) {
        User userCreate = new User();
        userCreate.setFirstName(firstname);
        userCreate.setUsername(username);
        userCreate.setDob(dob);
        userCreate.setEmail(email);
        userCreate.setPhoneNumber(phNum);
        userCreate.setGender(gender);
        userCreate.setPassword(password);
        this.accountArray.add(userCreate);
    }
}
