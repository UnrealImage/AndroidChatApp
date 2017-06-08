package aki.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Shadow on 2016/12/13.
 */
public class HeartService extends Service {
    // dynamic broadcast name
    public static final String BROADCAST_NEWMSG = "new_msg";
    public static final String BROADCAST_SHAKE = "shake";
    public static final String BROADCAST_NEWCONTACTVERIFY = "new_contact";
    public static final String BROADCAST_NEWREAD = "new_read";
    public static final String BROADCAST_NEWSET = "new_setting";
    public static final String BROADCAST_LOGOUT = "log_out";
    public static final String BROADCAST_NEWCONTACTREQUEST = "new_add_contact_request";

    public static LinkedHashMap<String, String> lastNewMessageNumber = new LinkedHashMap<>();
    // binder
    private final IBinder binder = new Binder() {
        HeartService getService() {
            return HeartService.this;
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    boolean keepHeart = true;
    // http connector
    HttpConnectController connectController = HttpConnectController.getInstance(this);

    // heart handler
    Handler heartHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (keepHeart) {
                try {
                    connectController.heart(responseHandler);
                    heartHandler.postDelayed(this, 5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    // heart response handler
    Handler responseHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
        try {
            switch (message.what) {
                case HttpConnectController.VAL_SUCCESS:
                    JSONObject messageObj = (JSONObject) message.obj;
                    Log.v("HeartMessage", messageObj.toString());

                    Iterator<String> keyIt = messageObj.keys();
                    JSONObject content;
                    Intent intent;
                    while (keyIt.hasNext()) {
                        String keyStr = keyIt.next();
                        Log.v("key", keyStr);
                        switch (keyStr) {
                            case HttpConnectController.KEY_NEWMSG:
                                content = messageObj.getJSONObject(keyStr);
                                Log.v("content", content.toString());
                                Iterator<String> messageIt = content.keys();
                                while (messageIt.hasNext()) {
                                    String senderId = messageIt.next();
                                    String number = content.getString(senderId);

                                    if (!lastNewMessageNumber.containsKey(senderId) || (lastNewMessageNumber.containsKey(senderId) && !lastNewMessageNumber.get(senderId).equals(number)) ) {
                                        intent = new Intent(BROADCAST_NEWMSG);
                                        intent.putExtra("id", senderId);
                                        intent.putExtra("number", number);
                                        sendBroadcast(intent);
                                        lastNewMessageNumber.put(senderId, number);
                                    }
                                }
                                break;
                            case HttpConnectController.KEY_NEWSHAKE:
                                JSONArray senderArray = messageObj.getJSONArray(keyStr);
                                for (int i = 0; i < senderArray.length(); ++i){
                                    intent = new Intent(BROADCAST_SHAKE);
                                    intent.putExtra("id", senderArray.getString(i));
                                    sendBroadcast(intent);
                                }
                                break;
                            case HttpConnectController.KEY_NEWCONTACTREQUEST:
                                content = messageObj.getJSONObject(keyStr);
                                if (content.length() > 0) {
                                    intent = new Intent(BROADCAST_NEWCONTACTREQUEST);
                                    sendBroadcast(intent);
                                }
                                break;
                            case HttpConnectController.KEY_NEWCONTACTVERIFY:
//                                content = messageObj.getJSONObject(keyStr);
//                                Iterator<String> contactIt = content.keys();
//                                while (contactIt.hasNext()) {
//                                    String contactId = contactIt.next();
//                                    JSONObject contactInfo = content.getJSONObject(contactId);
//
//                                    intent = new Intent(BROADCAST_NEWCONTACTVERIFY);
//                                    intent.putExtra("id", contactId);
//                                    intent.putExtra("name", contactInfo.getString(HttpConnectController.KEY_USERNAME));
//                                    intent.putExtra("signature", contactInfo.getString(HttpConnectController.KEY_SIGNATURE));
//                                    sendBroadcast(intent);
//                                }
                                content = messageObj.getJSONObject(keyStr);
                                if (content.length() > 0) {
                                    intent = new Intent(BROADCAST_NEWCONTACTVERIFY);
                                    sendBroadcast(intent);
                                }
                                break;
                            case HttpConnectController.KEY_NEWREAD:
                                JSONArray readMsgIdArr = messageObj.getJSONArray(keyStr);
                                if (readMsgIdArr.length() > 0) {
                                    intent = new Intent(BROADCAST_NEWREAD);
                                    sendBroadcast(intent);
                                }
                                break;
                            case HttpConnectController.KEY_NEWSETTING:
                                content = messageObj.getJSONObject(keyStr);
                                if (content.length() > 0) {
                                    intent = new Intent(BROADCAST_NEWSET);
                                    sendBroadcast(intent);
                                }
                                break;
                            default:
                                Log.v("default key", keyStr);
                                break;
                        }
                    }
                    break;
                case HttpConnectController.VAL_ILLEGAL:
                    break;
                case HttpConnectController.VAL_ERROR:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        }
    };
    @Override
    public void onCreate() {
        super.onCreate();

        keepHeart = true;
        heartHandler.post(runnable);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("Service:", "Start");
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        keepHeart = false;
    }
}
