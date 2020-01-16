package edu.upc.whatsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import edu.upc.whatsapp.comms.RPC;
import edu.upc.whatsapp.adapter.MyAdapter_users;
import entity.UserInfo;
import java.util.List;

public class d_UsersListActivity extends Activity {

  _GlobalState globalState;
  MyAdapter_users adapter;
  ProgressDialog progressDialog;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    System.out.println("On User list");
    globalState = (_GlobalState) getApplication();
    setContentView(R.layout.d_userslist);
    new DownloadUsers_Task().execute();

  }

  private class DownloadUsers_Task extends AsyncTask<Void, Void, List<UserInfo>> {

    @Override
    protected void onPreExecute() {
      progressDialog = ProgressDialog.show(d_UsersListActivity.this, "UsersListActivity",
        "downloading the users...");
    }

    @Override
    protected List<UserInfo> doInBackground(Void... nothing) {
      return RPC.allUserInfos();
    }

    @Override
    protected void onPostExecute(List<UserInfo> users) {
      progressDialog.dismiss();
      if (users == null) {
        toastShow("There's been an error downloading the users");
      } else {
          adapter = new MyAdapter_users(d_UsersListActivity.this, users);
          ListView listview = (ListView) findViewById(R.id.listView);
          listview.setAdapter(adapter);
          listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                  globalState.user_to_talk_to = (UserInfo) adapter.getItem(position);
                  Intent newIntent = new Intent(d_UsersListActivity.this, e_MessagesActivity.class);
                  startActivity(newIntent);
              }
          });
      }
    }
  }
private void toastShow(String text) {
    Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
    toast.setGravity(0, 0, 200);
    toast.show();
  }

}
