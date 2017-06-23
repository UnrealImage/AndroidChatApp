package aki.chat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
public class add_friend extends android.support.v4.app.Fragment {
    private static String NAME = "name";
    private static String ID = "id";
    private static String INFO = "info";
    //String[] testname = {"Noire", "Nep"};
    public static List<Map<String, Object>> list = new ArrayList<>();
    public static add_friend_adapter afa;
    private static HttpConnectController httpConnectController = HttpConnectController.getInstance(null);
    public static ArrayList<Contact> contactList;

    // + add friend
    public static BroadcastReceiver newFriendReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    switch (message.what) {
                        case HttpConnectController.VAL_SUCCESS:
                            list.clear();
                            contactList = (ArrayList<Contact>) message.obj;
                            for (int i = 0; i < contactList.size(); i++) {
                                Contact contact = contactList.get(i);
                                for (int j = 0; j < list.size(); j++) {
                                    if (!list.get(j).get(ID).toString().equals(contact.id)) {
                                        Map<String, Object> tmp = new LinkedHashMap<>();
                                        tmp.put(ID, contact.id);
                                        tmp.put(NAME, contact.name);
                                        list.add(tmp);
                                    }
                                }
                            }
                            if (afa != null) {
                                afa.notifyDataSetChanged();
                                refresh(context);
                            }
                            break;
                    }
                }
            };
            httpConnectController.getAddContactRequest(handler);
            Intent intent1 = new Intent("StaticBroadcast");
            intent1.putExtra("ContentTitle", "Chat");
            intent1.putExtra("ContentText", "你有一条新好友请求。");
            intent1.putExtra("Ticker", "你有一条新好友请求。");
            context.sendBroadcast(intent1);
        }
    };

    public static BroadcastReceiver newFriendVerify = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            friends.updateView();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_friend, null);
        TextView my_name = (TextView) view.findViewById(R.id.myName);
        my_name.setText(Contact.userName);

        Button add = (Button) view.findViewById(R.id.add);
        final EditText add_id = (EditText) view.findViewById(R.id.add_id);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        switch (message.what) {
                            case HttpConnectController.VAL_SUCCESS:
                                Toast.makeText(view.getContext(), "好友请求已发出。", Toast.LENGTH_SHORT).show();
                                break;
                            case HttpConnectController.VAL_ERROR:
                                Toast.makeText(view.getContext(), "此id不合法。", Toast.LENGTH_SHORT).show();
                                break;
                            case HttpConnectController.VAL_ILLEGAL:
                                Toast.makeText(view.getContext(), "该人已经是你好友。", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                httpConnectController.requestAddContact(add_id.getText().toString(), handler);
            }
        });

        ListView add_friend_list = (ListView) view.findViewById(R.id.add_friend_list);
        afa = new add_friend_adapter(list, view.getContext());
        add_friend_list.setAdapter(afa);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case HttpConnectController.VAL_SUCCESS:
                        list.clear();
                        contactList = (ArrayList<Contact>) message.obj;
                        for (int i = 0; i < contactList.size(); i++) {
                            Contact contact = contactList.get(i);
                            Map<String, Object> tmp = new LinkedHashMap<>();
                            tmp.put(ID, contact.id);
                            tmp.put(NAME, contact.name);
                            list.add(tmp);
                        }
                        afa.notifyDataSetChanged();
                        break;
                }
            }
        };
        httpConnectController.getAddContactRequest(handler);

        return view;
    }

    private static void refresh(Context context) {
        Log.v("refresh", "true");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.dismiss();
    }
}
