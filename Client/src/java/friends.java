package aki.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2016/12/13.
 */
public class friends extends android.support.v4.app.Fragment {
    //String[] testname = {"Aki", "Uni", "pj"};
    //String[] testinfo = {"QAQ", "(,,• ₃ •,,)", "♪(＾∀＾●)ﾉ"};
    private static String NAME = "name";
    private static String INFO = "info";
    private static String ID = "id";
    private static String NOT_READ = "not_read";
    private static String MSG = "msg";
    private static HttpConnectController httpConnectController = HttpConnectController.getInstance(null);
    public static ArrayList<Contact> contactList = new ArrayList<>();
    public static List<Map<String, Object>> list = new ArrayList<>();
    private static View view;
    public static friends_adapter fa = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.friends_list, null);
        TextView my_name = (TextView) view.findViewById(R.id.myName);
        my_name.setText(Contact.userName);


        ListView friends_list = (ListView) view.findViewById(R.id.friends_list);
        fa = new friends_adapter(list, view.getContext());
        friends_list.setAdapter(fa);
        updateView();
        fa.notifyDataSetChanged();

        friends_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), chat.class);
                Bundle bundle = new Bundle();
                bundle.putString(ID, list.get(i).get(ID).toString());
                bundle.putString(NAME, list.get(i).get(NAME).toString());
                intent.putExtras(bundle);
                try {
                    view.getContext().unregisterReceiver(recent.newMsgReceiver);
                }catch (Exception e) {
                    e.printStackTrace();
                }

                startActivity(intent);
                String zero = "0";
                int findID = 0;
                for (int j = 0; j < recent.list.size(); j++) {
                    if (recent.list.get(j).get(ID).toString().equals(list.get(i).get(ID).toString())) {
                        recent.list.get(j).put(NAME, list.get(i).get(NAME).toString());
                        recent.list.get(j).put(NOT_READ, zero);
                        recent.list.get(j).put(MSG, "你已读过所有消息。");

                        findID = 1;
                        break;
                    }
                }
                if (findID == 0) {
                    Map<String, Object> tmp = new LinkedHashMap<>();
                    tmp.put(ID, list.get(i).get(ID).toString());
                    tmp.put(NAME, list.get(i).get(NAME).toString());
                    tmp.put(MSG, "你已读过所有消息。");
                    tmp.put(NOT_READ, zero);
                    recent.list.add(tmp);
                }
            }
        });
        return view;
    }

    // + update friends list
    public static void updateView() {
        //ArrayList<Contact> contactList;
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case HttpConnectController.VAL_SUCCESS:
                        contactList = (ArrayList<Contact>) message.obj;
                        list = new ArrayList<>();
                        for (int i = 0; i < contactList.size(); i++) {
                            Contact contact = contactList.get(i);
                            Map<String, Object> temp = new LinkedHashMap<>();
                            temp.put(ID, contact.id);
                            temp.put(NAME, contact.name);
                            temp.put(INFO, contact.signature);
                            list.add(temp);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        httpConnectController.getContactList(handler);
    }
}
