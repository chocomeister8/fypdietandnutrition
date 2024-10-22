package BCEReference;

public class ControllerExample {
    public ControllerExample(){

    }
    public static void checkAccount(String firstname, String username, String dob, String email, String phNum, String gender){
        EntityExample entity = new EntityExample();

        //blah blah blah

        entity.createUserAccount(firstname,username,dob,email,phNum,gender);
    }
}
