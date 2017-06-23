package aki.chat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
public class recent extends android.support.v4.app.Fragment {
    //private static String LEFT_NAME = "left_name";
    //private static String RIGHT_NAME = "right_name";
    //private static String DEFAULT_NAME_NULL = "ef840a12187b20c1dd3aea3204206393";
    private static String ID = "id";
    private static String NAME = "name";
    private static String MSG = "msg";
    private static String NOT_READ = "not_read";
//    String[] testname = {"Aki", "Uni", "pj"};
//    String[] testmsg = {"I am your father.", "Deep dark fantasy.", "2333333"};
//    String[] testnotread = {"1", "2", "10"};
    HttpConnectController httpConnectController;
    public static recent_adapter ra;
    public static List<Map<String, Object>> list = new ArrayList<>();

    // + update recent list content
    public static BroadcastReceiver newMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String findID = bundle.get(ID).toString();
            int find = 0;
            for (int i = 0; i < list.size(); i++) {
                if (findID.equals(list.get(i).get(ID).toString())) {
                    list.get(i).put(MSG, "你有新的消息。");
                    list.get(i).put(NOT_READ, bundle.get("number").toString());
                    find = 1;
                }
            }
            if (find == 0) {
                Map<String, Object> tmp = new LinkedHashMap<>();
                tmp.put(ID, findID);
                tmp.put(NAME, RecentChat.idFindName(findID));
                tmp.put(MSG, "你有新的消息。");
                tmp.put(NOT_READ, bundle.get("number").toString());
                list.add(tmp);
            }
            ra.notifyDataSetChanged();
            refresh(context);

            Intent intent1 = new Intent("StaticBroadcast");
            intent1.putExtra("ContentTitle", "Chat");
            intent1.putExtra("ContentText", "你有一条新消息。");
            intent1.putExtra("Ticker", "你有一条新消息。");
            context.sendBroadcast(intent1);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recent_list, null);
        ListView recent_list = (ListView) view.findViewById(R.id.recent_list);
        ra = new recent_adapter(list, view.getContext());
        recent_list.setAdapter(ra);

        TextView my_name = (TextView) view.findViewById(R.id.myName);
        my_name.setText(Contact.userName);

        recent_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        recent_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("是否删除该条记录？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int p) {
                                list.remove(i);
                                ra.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //
                            }
                        })
                        .show();
                return true;
            }
        });
        httpConnectController = HttpConnectController.getInstance(view.getContext());
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
