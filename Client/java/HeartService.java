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
                                break;
                            case HttpConnectController.KEY_NEWSHAKE:
                                break;
                            case HttpConnectController.KEY_NEWCONTACTREQUEST:
                                break;
                            case HttpConnectController.KEY_NEWCONTACTVERIFY:
                                break;
                            case HttpConnectController.KEY_NEWREAD:
                                break;
                            case HttpConnectController.KEY_NEWSETTING:
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
