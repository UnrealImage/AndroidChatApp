package aki.chat;

import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Shadow on 2016/12/13.
 */
public class HttpConnectController {
    private static final String url = "http://118.89.33.45:9001";
//    private static final String url = "http://172.18.69.153:9001";
    public static final String TYPE_LOGIN = "/login";
    public static final String TYPE_REGISTER = "/register";
    public static final String TYPE_HEART = "/heart";
    public static final String TYPE_POSTMSG = "/postmsg";
    public static final String TYPE_GETMSG = "/getmsg";
    public static final String TYPE_CHANGE = "/change";
    public static final String TYPE_GETCONTACT = "/getcontext";

    public static final String KEY_USERID = "user_id";
    public static final String KEY_USERNAME = "user_name";
    public static final String KEY_SIGNATURE = "config";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NEWPASSWORD = "new_password";
    public static final String KEY_CONTACTID = "contact_id";
    public static final String KEY_DESTINATIONID = "to_user";
    public static final String KEY_MSGID = "msg_id";
    public static final String KEY_MSGCONTENT = "msg";
    public static final String KEY_MSGTYPE = "type";
    public static final String KEY_MSGTIME = "time";
    public static final String KEY_MSGDETAIL = "detail";
    public static final String KEY_NEWMSG = "new_msg";
    public static final String KEY_NEWSHAKE = "new_shake";
    public static final String KEY_NEWCONTACTREQUEST = "new_request";
    public static final String KEY_NEWCONTACTVERIFY = "new_friend";
    public static final String KEY_NEWREAD = "new_readed";
    public static final String KEY_NEWSETTING = "new_setting";
    public static final String KEY_LOGOUT = "log_out";

    public static final int VAL_SUCCESS = 200;
    public static final int VAL_ILLEGAL = 403;
    public static final int VAL_ERROR = 404;

    public static final String VAL_NORMAL = "0";
    public static final String VAL_SHAKE = "1";
    public static final String VAL_ADDCONTACT = "2";
    public static final String VAL_VERIFYADDCONTACT = "3";


    private static DBHelper dbHelper;
    private static HttpConnectController _instance;
    private HttpConnectController() {}
    public static HttpConnectController getInstance(Context context) {
        if (_instance == null) {
            _instance = new HttpConnectController();
        }
        return _instance;
    }
    public void initialDatabase(Context context) {
        if (Contact.userId != null) {
            dbHelper = new DBHelper(context);
        }
    }
    private void postMessage(final LinkedHashMap<String, String> obj, final String type, final Handler responseHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;

                try {
                    Log.v("URL:", url + type);
                    connection = (HttpURLConnection) ((new URL(url + type).openConnection()));
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                    String sendMessage = "";
                    boolean isFirst = true;
                    for (Map.Entry<String, String> entry : obj.entrySet()) {
                        if (!isFirst)
                            sendMessage += "&";
                        isFirst = false;
                        sendMessage += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8");
                    }
                    Log.v("sendMsg:", sendMessage);
                    os.writeBytes(sendMessage);
                    os.close();

                    JSONObject responseObj = new JSONObject();
                    if (connection.getResponseCode() == VAL_SUCCESS) {
                        InputStream is = connection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null)
                            response.append(line);
                        Log.v("response:", response.toString());
                        responseObj = new JSONObject(response.toString());
                        is.close();
                    }
                    connection.disconnect();

                    Message message = new Message();
                    message.what = connection.getResponseCode();
                    message.obj = responseObj;
                    responseHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void signIn(final String id, final String password, final Handler handler) {
        LinkedHashMap<String, String> user = new LinkedHashMap<>();
        user.put(KEY_USERID, id);
        user.put(KEY_PASSWORD, password);
        Handler signInHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Message newMessage = new Message();
                newMessage.what = message.what;
                switch (message.what) {
                    case VAL_SUCCESS:
                        try {
                            JSONObject myself = (JSONObject) message.obj;
                            newMessage.obj = message.obj;
                            handler.sendMessage(newMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case VAL_ILLEGAL:
                    case VAL_ERROR:
                        handler.sendMessage(newMessage);
                        break;
                }
            }
        };
        postMessage(user, TYPE_LOGIN, signInHandler);
    }
    public void register(String name, String password, String signature, Handler handler) {
        LinkedHashMap<String, String> messageObj = new LinkedHashMap<String, String>();
        messageObj.put("user_name", name);
        messageObj.put("password", password);
        messageObj.put("config", signature);
        postMessage(messageObj, HttpConnectController.TYPE_REGISTER, handler);
    }
    public void sendMessageTo(final Contact contact, final String content, final Handler handler) {
    }
    public void shake(Contact contact, Handler handler) {
        LinkedHashMap<String, String> shakeMessage = new LinkedHashMap<>();
        shakeMessage.put(KEY_USERID, Contact.userId);
        shakeMessage.put(KEY_PASSWORD, Contact.userPassword);
        shakeMessage.put(KEY_DESTINATIONID, contact.id);
        shakeMessage.put(KEY_MSGTYPE, VAL_SHAKE);
        shakeMessage.put(KEY_MSGCONTENT, "");
        postMessage(shakeMessage, TYPE_POSTMSG, handler);
    }
    public void settingChange(final String name, final String password, final String signature, final Handler handler) {
    }

    public void getAddContactRequest(final Handler handler) {
    }
    public void requestAddContact(String contactId, final Handler handler) {
    }
    public void verifyAddContact(final String contactId, final Handler handler) {
    }
    // get will send msg to server and set msg read
    public void getDialogList(final Contact contact, final Handler handler) {
    }
    // update won't send message to server or set message read, just find from SQLite database
    public void updateDialogList(Contact contact, Handler handler) {
    }

    public void getContactList(final Handler handler) {
    }
    public void heart(final Handler handler) {
    }
}
