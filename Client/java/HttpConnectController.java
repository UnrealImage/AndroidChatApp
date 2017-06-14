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
                            // initial user
                            Contact.userId = id;
                            Contact.userPassword = password;
                            Contact.userName = myself.getString(KEY_USERNAME);
                            Contact.userSignature = myself.getString(KEY_SIGNATURE);
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
        LinkedHashMap<String, String> sendMessage = new LinkedHashMap<>();
        sendMessage.put(KEY_USERID, Contact.userId);
        sendMessage.put(KEY_PASSWORD, Contact.userPassword);
        sendMessage.put(KEY_DESTINATIONID, contact.id);
        sendMessage.put(KEY_MSGTYPE, VAL_NORMAL);
        sendMessage.put(KEY_MSGCONTENT, content);
        Handler sendHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Message newMessage = new Message();
                newMessage.what = message.what;
                newMessage.obj = message.obj;
                switch (message.what) {
                    case VAL_SUCCESS:
                        try {
                            JSONObject dialogObj = (JSONObject) message.obj;
                            Dialog dialog = new Dialog(dialogObj.getString(KEY_MSGID),
                                    Contact.userId, contact.id, content,
                                    dialogObj.getString(KEY_MSGTIME), 0);
                            dbHelper.insertDialog(dialog);
                            handler.sendMessage(newMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case VAL_ERROR:
                    case VAL_ILLEGAL:
                        handler.sendMessage(newMessage);
                        break;
                }
            }
        };
        postMessage(sendMessage, TYPE_POSTMSG, sendHandler);
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
    public void refuseAddContact(String contactId, Handler handler) {
    }
    // get will send msg to server and set msg read
    public void getDialogList(final Contact contact, final Handler handler) {
        LinkedHashMap<String, String> getMessageRequest = new LinkedHashMap<>();
        getMessageRequest.put(KEY_USERID, Contact.userId);
        getMessageRequest.put(KEY_PASSWORD, Contact.userPassword);
        getMessageRequest.put(KEY_CONTACTID, contact.id);
        Handler dialogGetHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Message clientMessage = new Message();
                clientMessage.what = message.what;
                switch (message.what) {
                    case VAL_SUCCESS:
                        try {
                            JSONObject messageObj = (JSONObject) message.obj;
                            JSONArray messageIdArray = (messageObj).getJSONArray(KEY_MSGID);
                            for (int i = 0; i < messageIdArray.length(); ++i) {
                                String messageId = messageIdArray.getString(i);
                                JSONObject messageInfo = messageObj.getJSONObject(messageId);
                                String receiverId =  messageInfo.getString(KEY_DESTINATIONID);
                                String senderId = receiverId.equals(Contact.userId) ? contact.id : Contact.userId;
                                Dialog newDialog = new Dialog(messageId,
                                        senderId, receiverId,
                                        messageInfo.getString(KEY_MSGDETAIL),
                                        messageInfo.getString(KEY_MSGTIME), 1);
                                dbHelper.insertDialog(newDialog);
                            }
                            dbHelper.setDialogRead(contact);
                            ArrayList<Dialog> dialogList = dbHelper.getDialogList(contact);
                            clientMessage.obj = dialogList;
                            handler.sendMessage(clientMessage);
                            HeartService.lastNewMessageNumber.remove(contact.id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case VAL_ERROR:
                    case VAL_ILLEGAL:
                        handler.sendMessage(clientMessage);
                        break;
                }
            }
        };
        postMessage(getMessageRequest, TYPE_GETMSG, dialogGetHandler);
    }
    // update won't send message to server or set message read, just find from SQLite database
    public void updateDialogList(Contact contact, Handler handler) {
        ArrayList<Dialog> dialogList = dbHelper.getDialogList(contact);
        Message clientMessage = new Message();
        clientMessage.what = VAL_SUCCESS;
        clientMessage.obj = dialogList;
        handler.sendMessage(clientMessage);
    }

    public void getContactList(final Handler handler) {
        final ArrayList<Contact> contactList = dbHelper.getContactList();
        if (contactList.size() == 0) {
            LinkedHashMap<String, String> sendMsg = new LinkedHashMap<>();
            sendMsg.put(KEY_USERID, Contact.userId);
            sendMsg.put(KEY_PASSWORD, Contact.userPassword);
            Handler contactResponseHandler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    Message clientMessage = new Message();
                    clientMessage.what = message.what;
                    switch (message.what) {
                        case VAL_SUCCESS:
                            try {
                                ArrayList<Contact> contactList = new ArrayList<>();
                                JSONObject content = (JSONObject) message.obj;
                                JSONArray contactIdArray = content.getJSONArray(KEY_USERID);
                                for (int i = 0; i < contactIdArray.length(); ++i) {
                                    String id = contactIdArray.getString(i);
                                    JSONObject contactInfo = content.getJSONObject(id);
                                    Contact contact = new Contact(id, contactInfo.getString(KEY_USERNAME), contactInfo.getString(KEY_SIGNATURE));
                                    contactList.add(contact);
                                    // insert into database
                                    dbHelper.insertContact(contact);
                                }
                                clientMessage.obj = contactList;
                                handler.sendMessage(clientMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VAL_ILLEGAL:
                        case VAL_ERROR:
                            handler.sendMessage(clientMessage);
                            break;
                    }
                }
            };
            postMessage(sendMsg, TYPE_GETCONTACT, contactResponseHandler);
        } else {
            Message clientMessage = new Message();
            clientMessage.what = VAL_SUCCESS;
            clientMessage.obj = contactList;
            handler.sendMessage(clientMessage);
        }
    }
    public void heart(final Handler handler) {
        LinkedHashMap<String, String> heartMessage = new LinkedHashMap<>();
        heartMessage.put(KEY_USERID, Contact.userId);
        heartMessage.put(KEY_PASSWORD, Contact.userPassword);
        Log.v("id", Contact.userId);
        Log.v("password", Contact.userPassword);
        final Handler dbHeartHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Message newMessage = new Message();
                newMessage.what = message.what;
                newMessage.obj = message.obj;
                switch (message.what) {
                    case VAL_SUCCESS:
                        // unknown
                        try {
                            JSONObject messageObj = (JSONObject) message.obj;
                            Iterator<String> keyIt = messageObj.keys();
                            while (keyIt.hasNext()) {
                                String key = keyIt.next();
                                switch (key) {
                                    case KEY_NEWREAD:
                                        JSONArray readMsgArray = messageObj.getJSONArray(key);
                                        for (int i = 0; i < readMsgArray.length(); ++i) {
                                            String readMessageId = readMsgArray.getString(i);
                                            Dialog readDialog = dbHelper.queryDialog(readMessageId);
                                            readDialog.isRead = 1;
                                            dbHelper.updateDialog(readDialog);
                                        }
                                        break;
                                    case KEY_NEWSETTING:
                                        JSONObject settingObj = messageObj.getJSONObject(key);
                                        Iterator<String> settingIt = settingObj.keys();
                                        while (settingIt.hasNext()) {
                                            String id = settingIt.next();
                                            JSONObject info = settingObj.getJSONObject(id);
                                            Contact contact = dbHelper.queryContact(id);
                                            contact.name = info.getString(KEY_USERNAME);
                                            contact.signature = info.getString(KEY_SIGNATURE);
                                            dbHelper.updateContact(contact);
                                        }
                                        break;
                                    case KEY_NEWCONTACTVERIFY:
                                        JSONObject newContacts = messageObj.getJSONObject(key);
                                        Iterator<String> newContactsIt = newContacts.keys();
                                        while (newContactsIt.hasNext()) {
                                            String id = newContactsIt.next();
                                            JSONObject contactInfo = newContacts.getJSONObject(id);
                                            Contact contact = new Contact(id, contactInfo.getString(KEY_USERNAME
                                            ), contactInfo.getString(KEY_SIGNATURE));
                                            dbHelper.insertContact(contact);
                                        }
                                        break;
                                    case KEY_NEWCONTACTREQUEST:
                                        JSONObject addContactReqObj = messageObj.getJSONObject(key);
                                        Iterator<String> addContactIt = addContactReqObj.keys();
                                        while (addContactIt.hasNext()) {
                                            String id = addContactIt.next();
                                            JSONObject contactInfo = addContactReqObj.getJSONObject(id);
                                            Contact contact = new Contact(id, contactInfo.getString(KEY_USERNAME), contactInfo.getString(KEY_SIGNATURE));
                                            dbHelper.insertAddContactRequest(contact);
                                        }
                                        break;
                                }
                            }
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
        postMessage(heartMessage, HttpConnectController.TYPE_HEART, dbHeartHandler);
    }
}
