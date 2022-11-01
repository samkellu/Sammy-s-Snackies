package Sammys.Snackies;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public enum UserType {
    BUYER("buyer"), SELLER("seller"), CASHIER("cashier"), OWNER("owner");

    private String name;

    private UserType(String name){
        this.name = name;
    }

    public String toString(){
        return this.name;
    }

    public static UserType fromName(String name){
        switch(name){
            case "buyer":
                return BUYER;
            case "seller":
                return SELLER;
            case "cashier":
                return CASHIER;
            case "owner":
                return OWNER;
            default:
                System.out.println("Role not found, defaulting to buyer");
                return BUYER;
        }
    }



}

class UserLogin {
    private String username, password;
    private UserType type;

    public UserLogin(String username, String password, UserType type){
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public boolean verifyLogin(String user, String pass){
        if (this.username.equals(user) && this.password.equals(pass)){
            return true;
        }
        return false;
    }

    public UserType getType(){
        return this.type;
    }

    public String getUsername(){
        return this.username;
    }

    public String toString(){
        String retval = this.username + ", " + this.type.toString();
        return retval;
    }

    public String getPassword(){
        return this.password;
    }

    public static void writeUsersToFile(String fp, ArrayList<UserLogin> logins){
        ArrayList<HashMap<String, Object>> jsonData = new ArrayList<>();
        
        // TODO
        // this was unused, I think this is fully done but I'll leave it as a comment for now.
        // HashMap<String, Object> currencyData = new HashMap<String, Object>();
        

        for (UserLogin user : logins) {
            HashMap<String, Object> userData = new HashMap<String, Object>();
            userData.put("username", user.getUsername());
            userData.put("password", user.getPassword());
            userData.put("type", user.getType().toString());
            jsonData.add(userData);
        }
        
        // Attempts to write the JSONArray to file
        try (FileWriter fw = new FileWriter(fp)) {
            org.json.simple.JSONArray.writeJSONString(jsonData, fw);
            fw.flush();
            fw.close();
        } catch(IOException e) {
            System.out.println("Failed to write to file");
            e.printStackTrace();
        }
    }

    public static ArrayList<UserLogin> readFromFile(String fp) {

        ArrayList<UserLogin> retval = new ArrayList<UserLogin>();
        JSONParser parser = new JSONParser();

        try (FileReader fr = new FileReader(fp)) {
            Object obj = parser.parse(fr);
            JSONArray jsonData = (JSONArray) obj;

            for (Object userInfo : jsonData) {
                JSONObject jObj = (JSONObject) userInfo;
                String username = (String) jObj.get("username");
                String password = (String) jObj.get("password");
                UserType type = UserType.fromName(((String) jObj.get("type")));
                retval.add(new UserLogin(username, password, type));
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return retval;
    }
    

}