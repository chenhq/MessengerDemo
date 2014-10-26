package com.github.chenhq.massagedemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MessengerService extends Service {

    NotificationManager mNM;
    String TAG = "MessagerService";
    ArrayList<Messenger> mClients = new ArrayList<Messenger>();

    int mValue = 0;

    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_SET_VALUE = 3;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    Log.i(TAG, "Register client.");
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    Log.i(TAG, "Unregister client.");
                    break;
                case MSG_SET_VALUE:
                    mValue = msg.arg1;
                    Log.i(TAG, "Set Value.");
                    for (int i=mClients.size()-1; i>=0; i--) {
                        try {
                            mClients.get(i).send(Message.obtain(null, MSG_SET_VALUE, mValue, 0));
                        } catch (RemoteException e) {
                            mClients.remove(i);
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public MessengerService() {
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        showNotification();

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mNM.cancel(R.string.remote_service_started);
        Toast.makeText(this, R.string.remote_service_stoped, Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
        //throw new UnsupportedOperationException("Not yet implemented");
    }

   private void showNotification() {
//
//       CharSequence text = getText(R.string.remote_service_started);
//       Notification notification = new Notification(R.drawable.ic_launcher, text,
//               System.currentTimeMillis());
//       PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//               new Intent(this, ResourceBundle.Control.class, 0));
//       notification.setLatestEventInfo(this, getText(R.string.remote_service_started),
//               text, contentIntent);
//       mNM.notify(R.string.remote_service_started,notification);

       Log.i(TAG,"Service Starting....");
   }
}
