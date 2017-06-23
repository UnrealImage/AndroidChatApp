package aki.chat;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecentChat extends AppCompatActivity {
    private static String ID = "id";
    private static String NAME = "name";
    private static String INFO = "info";
    private String[] options = {"最近", "朋友", "添加朋友", "我"};
    private static String DEFAULT_NAME_NULL = "ef840a12187b20c1dd3aea3204206393";
    private int imageButton[] = {R.drawable.recent, R.drawable.friends, R.drawable.add_friend, R.drawable.i};
    private Class fragmentArray[] = {recent.class,friends.class,add_friend.class, i.class};
    private static HttpConnectController httpConnectController = HttpConnectController.getInstance(null);

    // + shake receiver
    BroadcastReceiver shakeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Vibrator vibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
            Bundle bundle = intent.getExtras();
//            Toast.makeText(context, idFindName(bundle.get("id").toString()) + "请求尽快回复。", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent("StaticBroadcast");
            intent1.putExtra("ContentTitle", "Chat");
            intent1.putExtra("ContentText", idFindName(bundle.get("id").toString()) + "请求尽快回复。");
            intent1.putExtra("Ticker", idFindName(bundle.get("id").toString()) + "请求尽快回复。");
            context.sendBroadcast(intent1);
        }
    };

    // + set receiver
    BroadcastReceiver newSetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    switch (message.what) {
                        case HttpConnectController.VAL_SUCCESS:
                            friends.contactList = (ArrayList<Contact>) message.obj;
                            friends.list = new ArrayList<>();
                            for (int i = 0; i < friends.contactList.size(); i++) {
                                Contact contact = friends.contactList.get(i);
                                Map<String, Object> temp = new LinkedHashMap<>();
                                temp.put(ID, contact.id);
                                temp.put(NAME, contact.name);
                                temp.put(INFO, contact.signature);
                                friends.list.add(temp);
                            }
                            for (int i = 0; i < friends.list.size(); i++) {
                                for (int j = 0; j < recent.list.size(); j++) {
                                    if (recent.list.get(j).get(ID).toString().equals(friends.list.get(i).get(ID).toString())) {
                                        recent.list.get(j).put(NAME, friends.list.get(i).get(NAME).toString());
                                    }
                                }
                            }
                            recent.ra.notifyDataSetChanged();
                            refresh(context);
                            break;
                        default:
                            break;
                    }
                }
            };
            httpConnectController.getContactList(handler);
            friends.fa.notifyDataSetChanged();
            refresh(context);


//            for (int i = 0; i < add_friend.list.size(); i++) {
//                if (findID.equals(add_friend.list.get(i).get(ID).toString())) {
//                    add_friend.list.get(i).put(NAME, bundle.get("name").toString());
//                }
//            }
//            add_friend.afa.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentTabHost fragmentTabHost = (FragmentTabHost) findViewById(R.id.options);
        fragmentTabHost.setup(this, getSupportFragmentManager(), R.id.main_content);

        for (int i = 0; i < options.length; i++) {
            TabHost.TabSpec spec = fragmentTabHost.newTabSpec(options[i]).setIndicator(getView(i));
            fragmentTabHost.addTab(spec, fragmentArray[i], null);
            fragmentTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.rc_option_background);
        }

        IntentFilter newFriendFilter = new IntentFilter();
        newFriendFilter.addAction(HeartService.BROADCAST_NEWCONTACTREQUEST);
        registerReceiver(add_friend.newFriendReceiver, newFriendFilter);

        IntentFilter newFriendVerify = new IntentFilter();
        newFriendVerify.addAction(HeartService.BROADCAST_NEWCONTACTVERIFY);
        registerReceiver(add_friend.newFriendVerify, newFriendVerify);

        IntentFilter newSetFilter = new IntentFilter();
        newSetFilter.addAction(HeartService.BROADCAST_NEWSET);
        registerReceiver(newSetReceiver, newSetFilter);

        IntentFilter shakeFilter = new IntentFilter();
        shakeFilter.addAction(HeartService.BROADCAST_SHAKE);
        registerReceiver(shakeReceiver, shakeFilter);

        IntentFilter newMsgFilter = new IntentFilter();
        newMsgFilter.addAction(HeartService.BROADCAST_NEWMSG);
        registerReceiver(recent.newMsgReceiver, newMsgFilter);

        friends.updateView();
    }

    private View getView(int i) {
        View view = View.inflate(RecentChat.this, R.layout.option, null);

        TextView option_text = (TextView) view.findViewById(R.id.option);
        ImageView option_pic = (ImageView) view.findViewById(R.id.button_pic);

        option_pic.setImageResource(imageButton[i]);
        option_text.setText(options[i]);
        return view;
    }

    public static String idFindName(String id) {
        String name = DEFAULT_NAME_NULL;
        for (int i = 0; i < friends.contactList.size(); i++) {
            if (friends.contactList.get(i).id.equals(id)) {
                name = friends.contactList.get(i).name;
                break;
            }
        }
        return name;
    }

    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(add_friend.newFriendReceiver);
            unregisterReceiver(add_friend.newFriendVerify);
            unregisterReceiver(newSetReceiver);
            unregisterReceiver(shakeReceiver);
            unregisterReceiver(recent.newMsgReceiver);
        }catch(Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //
    }

    private static void refresh(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.dismiss();
    }
}
