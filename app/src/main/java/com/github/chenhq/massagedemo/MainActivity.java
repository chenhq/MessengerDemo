package com.github.chenhq.massagedemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    String TAG = "MainActivity";
    Messenger mService = null;
    boolean mIsBound;

    TextView mCallbackText;
    TextView mTextViewValue;
    Button btn_bind;
    Button btn_unbind;
    Button btn_setValue;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MessengerService.MSG_SET_VALUE:
                    mCallbackText.setText("Received from service: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Log.i(TAG, "Service Attached.");
            mCallbackText.setText("Attached.");

            try {
                Message msg = Message.obtain(null, MessengerService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                msg = Message.obtain(null, MessengerService.MSG_SET_VALUE, 100, 0);
                mService.send(msg);

            } catch (RemoteException e) {
                Log.i(TAG, "RemoteException: some errors;");
            }

            //Toast.makeText(Binding.this, R.string.remote_service_connected, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.i(TAG, "Service Disconnected.");
            mCallbackText.setText("Disconnected.");

        }
    };

    public void doBindService() {
        Intent intent = new Intent("com.github.chenhq.messengeservice.ACTION_BIND");

        ComponentName component = new ComponentName("com.github.chenhq.massagedemo",
                "com.github.chenhq.massagedemo.MessengerService");
        intent.setComponent(component);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.i(TAG, "Service Binding ...");
        mCallbackText.setText("Binding...");
    }

    public void doUnbindService() {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            MessengerService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        unbindService(mConnection);
        mIsBound = false;
        Log.i(TAG, "Service Unbinding ...");
        mCallbackText.setText("Unbinding...");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_bind = (Button) findViewById(R.id.button_bind);
        btn_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBindService();
            }
        });

        btn_unbind = (Button) findViewById(R.id.button_unbind);
        btn_unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUnbindService();
            }
        });

        mCallbackText = (TextView) findViewById(R.id.textView);


        btn_setValue = (Button) findViewById(R.id.button_set_value);
        btn_setValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextViewValue = (TextView) findViewById(R.id.textView_value);
                mTextViewValue.getText();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
