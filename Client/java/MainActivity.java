package aki.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public class MainActivity extends AppCompatActivity {
    public static final String SALT = "Ca(OH)2";
    private EditText userIdEdit;
    private EditText passwordEdit;
    private Button signIn;
    private Button signUp;
    private HttpConnectController connectController;
    // handle the response from server
    private Handler responseHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            try {
                switch (message.what) {
                    case HttpConnectController.VAL_SUCCESS:
                        // start user interface
                        JSONObject messageObj = (JSONObject) message.obj;
                        jumpToUserInterface(Contact.userId,
                                Contact.userName,
                                Contact.userPassword,
                                Contact.userSignature);
                        Log.v("UserInfo:", messageObj.toString());
                        break;
                    case HttpConnectController.VAL_ILLEGAL:
                        Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        break;
                    case HttpConnectController.VAL_ERROR:
                        Toast.makeText(MainActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // shared preference store the user info
    private static final String PREFERENCE_FILE = "preference";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.getBoolean(HttpConnectController.KEY_LOGOUT) == true) {
            editor.clear();
            editor.apply();
        }
        // if user has already sign in
        if (sharedPreferences.contains("userId") && sharedPreferences.getString("userId", "userId") != "") {
            Log.v("userId", sharedPreferences.getString("userId", "userId"));
            connectController.signIn(sharedPreferences.getString("userId", "userId"), sharedPreferences.getString("userPassword", "userPassword"), responseHandler);
        }
    }
    private void bindView() {
        userIdEdit = (EditText) findViewById(R.id.user_id);
        passwordEdit = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.sign_in);
        signUp = (Button) findViewById(R.id.sign_up);
        connectController = HttpConnectController.getInstance(this);
        // shared preference
        sharedPreferences = MainActivity.this.getSharedPreferences(PREFERENCE_FILE, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start sign in
                String userId = userIdEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if (userId.equals("")) {
                    Toast.makeText(MainActivity.this, "用户ID不能为空", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    password = MD5tool.MD5(SALT + password);
                    connectController.signIn(userId, password, responseHandler);
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start sign up activity
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });
    }
    private void jumpToUserInterface(String userId, String userName, String userPassword, String userSignature) {
        // record user to shared preference
        editor.putString("userId", userId);
        editor.putString("userName", userName);
        editor.putString("userPassword", userPassword);
        editor.putString("userSignature", userSignature);
        editor.apply();
        // init database
        connectController.initialDatabase(MainActivity.this);
        // start activity
        Intent intent = new Intent(MainActivity.this, RecentChat.class);
        startActivity(intent);
        // start heart service
        startService(new Intent(MainActivity.this, HeartService.class));
    }

}
