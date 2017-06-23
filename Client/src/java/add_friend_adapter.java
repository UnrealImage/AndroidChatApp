package aki.chat;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
public class add_friend_adapter extends BaseAdapter {
    private List<Map<String, Object>> _list;
    private Context _context;
    private static String NAME = "name";
    private static String ID = "id";
    private static String INFO = "info";
    HttpConnectController httpConnectController;

    public add_friend_adapter(List<Map<String, Object>> list, Context context) {
        this._context = context;
        this._list = list;
        httpConnectController = HttpConnectController.getInstance(this._context);
    }

    @Override
    public int getCount() {
        if (_list == null) {
            return 0;
        }
        return _list.size();
    }

    @Override
    public  Object getItem(int i) {
        if (_list == null) {
            return null;
        }
        return _list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // + button
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View _view;
        ViewHolder viewHolder;
        if (view == null) {
            _view = LayoutInflater.from(_context).inflate(R.layout.add_friend_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) _view.findViewById(R.id.add_friend_name);
            viewHolder.permit = (Button) _view.findViewById(R.id.permit);
            viewHolder.refuse = (Button) _view.findViewById(R.id.refuse);
            _view.setTag(viewHolder);
        } else {
            _view = view;
            viewHolder = (ViewHolder) _view.getTag();
        }
        viewHolder.name.setText(_list.get(i).get(NAME).toString());
        viewHolder.permit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        switch (message.what) {
                            case HttpConnectController.VAL_SUCCESS:
                                friends.updateView();
                                _list.remove(i);
                                notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }
                    }
                };
                httpConnectController.verifyAddContact(_list.get(i).get(ID).toString(), handler);
            }
        });
        viewHolder.refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        switch (message.what) {
                            case HttpConnectController.VAL_SUCCESS:
                                _list.remove(i);
                                notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }
                    }
                };
                httpConnectController.refuseAddContact(_list.get(i).get(ID).toString(), handler);
            }
        });
        return _view;
    }

    public class ViewHolder {
        private TextView name;
        private Button permit;
        private Button refuse;
    }
}
