package aki.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dk.view.drop.CoverManager;
import com.dk.view.drop.WaterDrop;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
public class friends_adapter extends BaseAdapter {
    private List<Map<String, Object>> _list;
    private Context _context;
    private static String NAME = "name";
    private static String INFO = "info";

    public friends_adapter(List<Map<String, Object>> list, Context context) {
        this._context = context;
        this._list = list;
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View _view;
        ViewHolder viewHolder;
        if (view == null) {
            _view = LayoutInflater.from(_context).inflate(R.layout.friends_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) _view.findViewById(R.id.friend_name);
            viewHolder.info = (TextView) _view.findViewById(R.id.friend_info);
            _view.setTag(viewHolder);
        } else {
            _view = view;
            viewHolder = (ViewHolder) _view.getTag();
        }
        viewHolder.name.setText(_list.get(i).get(NAME).toString());
        viewHolder.info.setText(_list.get(i).get(INFO).toString());

        return _view;
    }

    public class ViewHolder {
        private TextView name;
        private TextView info;
    }
}
