package edu.upc.whatsapp;

import edu.upc.whatsapp.comms.RPC;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import edu.upc.whatsapp.service.PushService;
import entity.User;
import entity.UserInfo;

public class c_RegistrationActivity extends Activity implements View.OnClickListener {

  _GlobalState globalState;
  ProgressDialog progressDialog;
  User user;
  OperationPerformer operationPerformer;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    globalState = (_GlobalState)getApplication();
    setContentView(R.layout.c_registration);
    ((Button) findViewById(R.id.editregistrationButton)).setOnClickListener(this);
  }

  public void onClick(View arg0) {
    if (arg0 == findViewById(R.id.editregistrationButton)) {

      //...
      TextView login = findViewById(R.id.LoginText);
      TextView password = findViewById(R.id.passwordText);
      TextView name = findViewById(R.id.NameText);
      TextView surname = findViewById(R.id.SurnameText);
      TextView email = findViewById(R.id.emailText);
      UserInfo userInfo = new UserInfo();
      userInfo.setName(name.getText().toString());
      userInfo.setSurname(surname.getText().toString());
      user = new User();
      user.setLogin(login.getText().toString());
      user.setPassword(password.getText().toString());
      user.setEmail(email.getText().toString());
      user.setUserInfo(userInfo);

      progressDialog = ProgressDialog.show(this, "RegistrationActivity", "Registering for service...");
      // if there's still a running thread doing something, we don't create a new one
      if (operationPerformer == null) {
        operationPerformer = new OperationPerformer();
        operationPerformer.start();
      }
    }
  }

  private class OperationPerformer extends Thread {

    @Override
    public void run() {
      Message msg = handler.obtainMessage();
      Bundle b = new Bundle();

      //...
      UserInfo userInfo = RPC.registration(user);
      b.putSerializable("userInfo",userInfo);
      msg.setData(b);
      handler.sendMessage(msg);
    }
  }

  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {

      operationPerformer = null;
      progressDialog.dismiss();

      UserInfo userInfo = (UserInfo) msg.getData().getSerializable("userInfo");

      if (userInfo.getId() >= 0) {
        toastShow("Registration successful");

        //...
        Intent newIntent  = new Intent(c_RegistrationActivity.this,b_LoginActivity.class);
        startActivity(newIntent);
        finish();
      }
      else if (userInfo.getId() == -1) {
        toastShow("Registration unsuccessful,\nlogin already used by another user");
      }
      else if (userInfo.getId() == -2) {
        toastShow("Not registered, connection problem due to: " + userInfo.getName());
        System.out.println("--------------------------------------------------");
        System.out.println("error!!!");
        System.out.println(userInfo.getName());
        System.out.println("--------------------------------------------------");
      }
    }
  };

  private void toastShow(String text) {
    Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
    toast.setGravity(0, 0, 200);
    toast.show();
  }
}
