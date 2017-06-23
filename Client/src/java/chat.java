package aki.chat;

import android.app.*;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
public class chat extends AppCompatActivity{
    //private String[] testtext = {"这句话是用来测试的需要写的很长但我又不知道该说点什么好烦啊QAQ", "测试", "感觉不错0v0"};
    //private String[] testtag = {"left", "right", "left"};
    private static String TEXT_LEFT = "text_left";
    private static String TEXT_RIGHT = "text_right";
    private static String NAME = "name";
    private static String ID = "id";
    private static String READ = "read";
    private static String MSG = "msg";
    private static String NOT_READ = "not_read";
    //private static String CLEAR = "clear";
    //private static String CLEAR_DATA_COUNT = "clear_data_count";
    private static HttpConnectController httpConnectController;
    public static ArrayList<Dialog> dialogList;
    public static Map<String, List<Map<String, Object>>> who = new LinkedHashMap<>();
    //public static List<Map<String, Object>> list;
    public static chat_adapter ca;
    private static String nowChatID;
    private SensorManager sensorManager = null;
    private Sensor shakeSensor = null;
    private int pauseFlag = 0;
    private static int recent50;
    private List<Map<String, Object>> nowList = new ArrayList<>();

    private BroadcastReceiver newReadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    switch (message.what) {
                        case HttpConnectController.VAL_SUCCESS:
                            dialogList = (ArrayList<Dialog>) message.obj;
                            List<Map<String, Object>> list = new ArrayList<>();
                            if (dialogList.size() <= 50) {
                                recent50 = 0;
                            } else {
                                recent50 = dialogList.size() - 50;
                            }
                            for (int i = recent50; i < dialogList.size(); i++) {
                                Dialog dialog = dialogList.get(i);
                                Map<String, Object> temp = new LinkedHashMap<>();
                                if (Contact.userId.equals(dialog.senderId)) {
                                    temp.put(TEXT_RIGHT, dialog.content);
                                    temp.put(TEXT_LEFT, null);
                                    temp.put(READ, dialog.isRead);
                                } else if (Contact.userId.equals((dialog.receiverId))) {
                                    temp.put(TEXT_LEFT, dialog.content);
                                    temp.put(TEXT_RIGHT, null);
                                }
                                list.add(temp);
                            }
                            who.get(nowChatID).clear();
                            who.get(nowChatID).addAll(list);
                            ca.notifyDataSetChanged();
                            refresh();
                            break;
                        default:
                            break;
                    }
                }
            };
            Contact contact = new Contact(nowChatID, "", "");
            httpConnectController.updateDialogList(contact, handler);
        }
    };

    private BroadcastReceiver newMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            String findID = bundle.get(ID).toString();

            if (findID.equals(nowChatID)) {
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        switch (message.what) {
                            case HttpConnectController.VAL_SUCCESS:
                                dialogList = (ArrayList<Dialog>) message.obj;
                                List<Map<String, Object>> list = new ArrayList<>();
                                if (dialogList.size() <= 50) {
                                    recent50 = 0;
                                } else {
                                    recent50 = dialogList.size() - 50;
                                }
                                for (int i = recent50; i < dialogList.size(); i++) {
                                    Dialog dialog = dialogList.get(i);
                                    Map<String, Object> temp = new LinkedHashMap<>();
                                    if (Contact.userId.equals(dialog.senderId)) {
                                        temp.put(TEXT_RIGHT, dialog.content);
                                        temp.put(TEXT_LEFT, null);
                                        temp.put(READ, dialog.isRead);
                                    } else if (Contact.userId.equals((dialog.receiverId))) {
                                        temp.put(TEXT_LEFT, dialog.content);
                                        temp.put(TEXT_RIGHT, null);
                                    }
                                    list.add(temp);
                                }
                                who.get(nowChatID).clear();
                                who.get(nowChatID).addAll(list);
                                ca.notifyDataSetChanged();
                                refresh();
                                break;
                            default:
                                break;
                        }
                    }
                };
                Contact contact = new Contact(nowChatID, "", "");
                httpConnectController.getDialogList(contact, handler);
            } else {
                int find = 0;
                for (int i = 0; i < recent.list.size(); i++) {
                    if (findID.equals(recent.list.get(i).get(ID).toString())) {
                        recent.list.get(i).put(MSG, "你有新的消息。");
                        recent.list.get(i).put(NOT_READ, bundle.get("number").toString());
                        find = 1;
                    }
                }
                if (find == 0) {
                    Map<String, Object> tmp = new LinkedHashMap<>();
                    tmp.put(ID, findID);
                    tmp.put(NAME, RecentChat.idFindName(findID));
                    tmp.put(MSG, "你有新的消息。");
                    tmp.put(NOT_READ, bundle.get("number").toString());
                    recent.list.add(tmp);
                }
                recent.ra.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        nowChatID = bundle.getString(ID).toString();
        register();


        final EditText editText = (EditText) findViewById(R.id.text);
        Button send = (Button) findViewById(R.id.send);
        Button shake = (Button) findViewById(R.id.shake);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().equals("")) {
                    Contact contact = new Contact(nowChatID, bundle.get(NAME).toString(), "");
                    final String content = editText.getText().toString();
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            switch (message.what) {
                                case HttpConnectController.VAL_SUCCESS:
                                    Map<String, Object> tmp = new LinkedHashMap<>();
                                    tmp.put(TEXT_RIGHT, content);
                                    tmp.put(TEXT_LEFT, null);
                                    tmp.put(READ, (int)-1);
                                    who.get(nowChatID).add(tmp);
                                    nowList = who.get(nowChatID);
                                    ca.list = nowList;
                                    ca.notifyDataSetChanged();
                                    refresh();
                                    editText.setText("");
                                    break;
                                default:
                                    break;
                            }
                        }
                    };
                    httpConnectController.sendMessageTo(contact, content, handler);
                }
            }
        });

        shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder tip = new AlertDialog.Builder(chat.this);
                tip.setTitle("Shake");
                tip.setMessage("摇一摇，震动对方手机");
                tip.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sensorManager.unregisterListener(sensorEventListener);
                    }
                });
                tip.show();
                sensorManager.registerListener(sensorEventListener, shakeSensor, SensorManager.SENSOR_DELAY_GAME);
            }
        });

        httpConnectController = HttpConnectController.getInstance(this);

        TextView left_name = (TextView) findViewById(R.id.left_name);
        left_name.setText(bundle.get(NAME).toString());

        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        final ListView chat_list = (ListView) findViewById(R.id.chat_list);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case HttpConnectController.VAL_SUCCESS:
                        dialogList = (ArrayList<Dialog>) message.obj;
                        List<Map<String, Object>> list = new ArrayList<>();
                        if (dialogList.size() <= 50) {
                            recent50 = 0;
                        } else {
                            recent50 = dialogList.size() - 50;
                        }
                        for (int i = recent50; i < dialogList.size(); i++) {
                            Dialog dialog = dialogList.get(i);
                            Map<String, Object> temp = new LinkedHashMap<>();
                            if (Contact.userId.equals(dialog.senderId)) {
                                temp.put(TEXT_RIGHT, dialog.content);
                                temp.put(TEXT_LEFT, null);
                                temp.put(READ, dialog.isRead);
                            } else if (Contact.userId.equals((dialog.receiverId))) {
                                temp.put(TEXT_LEFT, dialog.content);
                                temp.put(TEXT_RIGHT, null);
                            }
                            list.add(temp);
                        }
                        who.put(nowChatID, list);
                        nowList = who.get(nowChatID);
                        ca = new chat_adapter(nowList, chat.this);
                        chat_list.setAdapter(ca);
                        break;
                    default:
                        break;
                }
            }
        };
        Contact contact = new Contact(nowChatID, bundle.get(NAME).toString(), "");
        httpConnectController.getDialogList(contact, handler);
    }

    @Override
    protected void onResume() {
        register();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseFlag = 1;
        unregister();
    }

    @Override
    protected void onDestroy() {
        if (pauseFlag == 0) {
            unregister();
        }
        super.onDestroy();
    }

    private void register() {
        IntentFilter newMsgFilter = new IntentFilter();
        newMsgFilter.addAction(HeartService.BROADCAST_NEWMSG);
        registerReceiver(newMsgReceiver, newMsgFilter);

        IntentFilter newReadFilter = new IntentFilter();
        newReadFilter.addAction(HeartService.BROADCAST_NEWREAD);
        registerReceiver(newReadReceiver, newReadFilter);
    }
    private void unregister() {
        unregisterReceiver(newMsgReceiver);
        unregisterReceiver(newReadReceiver);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] lastAcc = {0, 0, 0};
        float[] accValues = {0, 0, 0};
        float[] magValues = {0, 0, 0};
        boolean coldtime = false;
        Handler coldHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                coldtime = false;
            }
        };

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (!coldtime) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        System.arraycopy(accValues, 0, lastAcc, 0, accValues.length);
                        System.arraycopy(sensorEvent.values, 0, accValues, 0, sensorEvent.values.length);
                        if (Math.abs(accValues[0] - lastAcc[0]) > 23 ||
                                Math.abs(accValues[1] - lastAcc[1]) > 23 ||
                                Math.abs(accValues[2] - lastAcc[2]) > 23) {
                            Contact contact = new Contact(nowChatID, "", "");
                            Handler handler = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    switch (message.what) {
                                        case HttpConnectController.VAL_SUCCESS:
                                            Toast.makeText(chat.this, "已发出震动", Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            };
                            httpConnectController.shake(contact, handler);
                            coldtime = true;
                            handler.postDelayed(runnable, 2000);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent(chat.this, RecentChat.class);
            IntentFilter newMsgFilter = new IntentFilter();
            newMsgFilter.addAction(HeartService.BROADCAST_NEWMSG);
            registerReceiver(recent.newMsgReceiver, newMsgFilter);
            startActivity(intent);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        Log.v("refresh", "true");
        AlertDialog.Builder builder = new AlertDialog.Builder(chat.this);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.dismiss();
    }

    private int max(int a, int b) {
        if (a < b) return b;
        else return a;
    }

}
