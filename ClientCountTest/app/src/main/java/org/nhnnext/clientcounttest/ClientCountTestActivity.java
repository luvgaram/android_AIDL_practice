package org.nhnnext.clientcounttest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.nhnnext.servicecounttest.ICountService;
import org.nhnnext.servicecounttest.ICountServiceCallback;

public class ClientCountTestActivity extends AppCompatActivity {
    private ICountService mBinder;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_count_test);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_start:
                intent = new Intent("org.nhnnext.service.COUNT");
                startService(intent);
                break;
            case R.id.btn_stop:
                intent = new Intent("org.nhnnext.service.COUNT");
                stopService(intent);
                break;
            case R.id.btn_bind:
                intent = new Intent("org.nhnnext.service.COUNT");
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind:
                try {
                    mBinder.unregisterCountCallback(mCountCalback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                if (mBinder != null) unbindService(mConnection);
                break;
            case R.id.btn_show_cur_num:
                if (mBinder != null) {
                    try {
                        int curNumber = mBinder.getCurNumber();
                        Toast.makeText(this, "Current Number : " + curNumber, Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    ICountServiceCallback mCountCalback = new ICountServiceCallback.Stub() {

        @Override
        public void onCountChanged(int changedCount) throws RemoteException {
            Log.e("countservice", "Client call onCountChanged " + changedCount);
        }
    };

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(getApplicationContext(), "Service Binding", Toast.LENGTH_SHORT).show();
            mBinder = ICountService.Stub.asInterface(service);

            try {
                mBinder.registerCountCallback(mCountCalback);
            } catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "Service Unbinding", Toast.LENGTH_SHORT).show();
            mBinder = null;
        }
    };
}