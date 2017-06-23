package aki.chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dk.view.drop.CoverManager;
import com.dk.view.drop.WaterDrop;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
public class recent_adapter extends BaseAdapter {
    private List<Map<String, Object>> _list;
    private Context _context;
    private static String NAME = "name";
    private static String MSG = "msg";
    private static String NOT_READ = "not_read";
    private static String CANCEL = "cancel";

    public recent_adapter(List<Map<String, Object>> list, Context context) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View _view;
        ViewHolder viewHolder;
        if (view == null) {
            _view = LayoutInflater.from(_context).inflate(R.layout.recent_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) _view.findViewById(R.id.recent_chat_name);
            viewHolder.recent_msg = (TextView) _view.findViewById(R.id.msg);
            viewHolder.not_read = (WaterDrop) _view.findViewById(R.id.not_read);
            _view.setTag(viewHolder);
        } else {
            _view = view;
            viewHolder = (ViewHolder) _view.getTag();
        }
        viewHolder.name.setText(_list.get(i).get(NAME).toString());
        viewHolder.recent_msg.setText(_list.get(i).get(MSG).toString());

        CoverManager.getInstance().init((Activity) _context);
        CoverManager.getInstance().setMaxDragDistance(250);
        CoverManager.getInstance().setEffectDuration(150);
        if (_list.get(i).get(NOT_READ).toString().equals("0")) {
            viewHolder.not_read.setVisibility(View.INVISIBLE);
        } else if (_list.get(i).get(NOT_READ).toString().equals(CANCEL)) {
            viewHolder.not_read.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.not_read.setText(_list.get(i).get(NOT_READ).toString());
        }


        viewHolder.not_read.setEffectResource(R.drawable.bubble1);
        viewHolder.not_read.setOnDragCompeteListener(new CoverManager.OnDragCompeteListener() {
            @Override
            public void onDragComplete() {
                _list.get(i).put(NOT_READ, CANCEL);
            }
        });
        return _view;
    }

    public class ViewHolder {
        private TextView name;
        private TextView recent_msg;
        private WaterDrop not_read;
    }
}
