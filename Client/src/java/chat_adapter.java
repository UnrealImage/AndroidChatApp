package aki.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/14.
 */
public class chat_adapter extends BaseAdapter {
    public static List<Map<String, Object>> list;
    private Context _context;
    private static String TEXT_LEFT = "text_left";
    private static String TEXT_RIGHT = "text_right";
    private static String READ = "read";
    //private static String CLEAR = "clear";
    private int left = 0;
    private int right = 0;

    public chat_adapter(List<Map<String, Object>> list, Context context) {
        this._context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public  Object getItem(int i) {
        if (list == null) {
            return null;
        }
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View _view;
        ViewHolder viewHolder;

        _view = LayoutInflater.from(_context).inflate(R.layout.chat_list_item, null);
        viewHolder = new ViewHolder();
        viewHolder.left_sentence = (TextView) _view.findViewById(R.id.left_sentence);
        viewHolder.right_sentence = (TextView) _view.findViewById(R.id.right_sentence);
        viewHolder.read = (TextView) _view.findViewById(R.id.read);
        _view.setTag(viewHolder);

        if (list.get(i).get(TEXT_LEFT) == null) {
            viewHolder.left_sentence.setVisibility(View.INVISIBLE);
            viewHolder.right_sentence.setText(list.get(i).get(TEXT_RIGHT).toString());
            if ((int)list.get(i).get(READ) > 0) {
                viewHolder.read.setText("已读");
            } else {
                viewHolder.read.setText("未读");
            }
        } else {
            viewHolder.right_sentence.setVisibility(View.INVISIBLE);
            viewHolder.left_sentence.setText(list.get(i).get(TEXT_LEFT).toString());
            viewHolder.read.setVisibility(View.GONE);
        }
        return _view;
    }

//    @Override
//    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
//        return view;
//    }


    public class ViewHolder {
        private TextView left_sentence;
        private TextView right_sentence;
        private TextView read;
    }

}
