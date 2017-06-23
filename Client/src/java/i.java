package aki.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

/**
 * Created by Administrator on 2016/12/13.
 */
public class i extends android.support.v4.app.Fragment {
    HttpConnectController httpConnectController;

    // + button
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.i, null);

        TextView myID = (TextView) view.findViewById(R.id.myID);
        myID.setText(Contact.userId);

        final TextView my_name = (TextView) view.findViewById(R.id.myName);
        my_name.setText(Contact.userName);

        final EditText new_name = (EditText) view.findViewById(R.id.new_name);
        final EditText new_password = (EditText) view.findViewById(R.id.new_password);
        final EditText confirm_password = (EditText) view.findViewById(R.id.confirm_password);
        final EditText new_info = (EditText) view.findViewById(R.id.new_info);
        new_info.setHint(Contact.userSignature);
        Button submit = (Button) view.findViewById(R.id.submit);
        Button log_out = (Button) view.findViewById(R.id.log_out);

        httpConnectController = HttpConnectController.getInstance(view.getContext());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        switch (message.what) {
                            case HttpConnectController.VAL_SUCCESS:
                                //TextView my_name = (TextView) view.findViewById(R.id.myName);
                                my_name.setText(Contact.userName);
                                Toast.makeText(view.getContext(), "更改成功", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                };
                if (new_password.getText().toString().equals(confirm_password.getText().toString())) {
                    String name;
                    String info;
                    if (new_name.getText().toString().equals("")) {
                        name = Contact.userName;
                    } else {
                        name = new_name.getText().toString();
                    }
                    if (new_info.getText().toString().equals("")) {
                        info = Contact.userSignature;
                    } else {
                        info = new_info.getText().toString();
                    }
                    if (new_password.getText().toString().equals("") && confirm_password.getText().toString().equals("")) {
                        httpConnectController.settingChange(name, Contact.userPassword, info, handler);
                    } else if (new_password.getText().toString().length() < 6){
                        Toast.makeText(view.getContext(), "密码不能少于6位。", Toast.LENGTH_SHORT).show();
                    } else {
                        String password = MD5tool.MD5(MainActivity.SALT + new_password.getText().toString());
                        httpConnectController.settingChange(name, password, info, handler);
                    }
                } else {
                    Toast.makeText(view.getContext(), "密码与确认密码不同。", Toast.LENGTH_SHORT).show();
                }
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // finish and return
                recent.list.clear();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra(HttpConnectController.KEY_LOGOUT, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                view.getContext().stopService(new Intent(view.getContext(), HeartService.class));
            }
        });
        return view;
    }
}
