package aki.chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.LinkedHashMap;

/**
 * Created by Shadow on 2016/12/13.
 */
public class SignUpActivity extends Activity {
    private EditText userNameEdit;
    private EditText passwordEdit;
    private EditText confirmPasswordEdit;
    private EditText signatureEdit;
    private Button signUp;
    private Button clear;
    private HttpConnectController connectController;
    // handle the response from server
    private Handler responseHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            try {
                JSONObject messageObj = (JSONObject) message.obj;
                switch (message.what) {
                    case HttpConnectController.VAL_SUCCESS:
                        // start user interface
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setTitle("注册成功");
                        builder.setMessage("您的用户ID为：" + messageObj.getString(HttpConnectController.KEY_USERID));
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            }
                        });
                        builder.show();
                        break;
                    case HttpConnectController.VAL_ILLEGAL:
                        Toast.makeText(SignUpActivity.this, "错误：" + messageObj.getString(HttpConnectController.KEY_MSGCONTENT), Toast.LENGTH_SHORT).show();
                        break;
                    case HttpConnectController.VAL_ERROR:
                        Toast.makeText(SignUpActivity.this, "错误：" + messageObj.getString(HttpConnectController.KEY_MSGCONTENT), Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        bindView();
    }

    private void bindView() {
        userNameEdit = (EditText) findViewById(R.id.user_name);
        passwordEdit = (EditText) findViewById(R.id.password);
        confirmPasswordEdit = (EditText) findViewById(R.id.confirm_password);
        signatureEdit = (EditText) findViewById(R.id.signature);
        signUp = (Button) findViewById(R.id.sign_up);
        clear = (Button) findViewById(R.id.clear);
        connectController = HttpConnectController.getInstance(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sign up
                String userName = userNameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String confirmPassword = confirmPasswordEdit.getText().toString();
                String signature = signatureEdit.getText().toString();

                if (userName.equals("")) {
                    Toast.makeText(SignUpActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(SignUpActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "密码至少为6位", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "密码与确认密码不统一", Toast.LENGTH_SHORT).show();
                } else {
                    password = MD5tool.MD5(MainActivity.SALT + password);
                    connectController.register(userName, password, signature, responseHandler);
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameEdit.setText("");
                passwordEdit.setText("");
                confirmPasswordEdit.setText("");
                signatureEdit.setText("");
            }
        });
    }
}
