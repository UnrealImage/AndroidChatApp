package aki.chat;

import java.util.ArrayList;

/**
 * Created by Shadow on 2016/12/13.
 */
public class Contact {
    // current user info
    public static String userId;
    public static String userName;
    public static String userPassword;
    public static String userSignature;

    public String id;
    public String name;
    public String signature;
    Contact(String id, String name, String signature) {
        this.id = id;
        this.name = name;
        this.signature = signature;
    }
}
