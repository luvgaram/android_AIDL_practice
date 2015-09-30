package org.nhnnext.servicecounttest;

    import android.app.Service;
    import android.content.Intent;
    import android.os.IBinder;
    import android.os.RemoteCallbackList;
    import android.os.RemoteException;
    import android.util.Log;

/**
 * Created by eunjooim on 15. 9. 30.
 */
public class CountService extends Service {
    public int mCurNum = 0;
    public Thread mCountThread;
    final RemoteCallbackList<ICountServiceCallback> mCallbackList = new RemoteCallbackList<>();

    ICountService.Stub mBinder = new ICountService.Stub() {

        @Override
        public int getCurNumber() throws RemoteException {
            return mCurNum;
        }

        @Override
        public int sum(int a, int b) throws RemoteException {
            return a + b;
        }

        @Override
        public boolean registerCountCallback(ICountServiceCallback callback) throws RemoteException {
            if (callback != null) return mCallbackList.register(callback);
            else return false;
        }

        @Override
        public boolean unregisterCountCallback(ICountServiceCallback callback) throws  RemoteException {
            if (callback != null) return mCallbackList.unregister(callback);
            else return false;
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("countservice", "onStartCommand");

        if (mCountThread == null) {
            mCountThread = new Thread("Count Thread") {
                public void run() {
                    try {
                        while (!Thread.currentThread().isInterrupted()) {
                            mCurNum++;

                            int count = mCallbackList.beginBroadcast();

                            for (int i = 0; i < count; i++) {
                                try {
                                    ICountServiceCallback callback =
                                            ((ICountServiceCallback) mCallbackList.getBroadcastItem(i));

                                    if (callback != null) {
                                        Log.i("countservice", "Service call onCountChanged");
                                        callback.onCountChanged(mCurNum);
                                    }
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                            mCallbackList.finishBroadcast();
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            mCountThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("countservice", "onDestroy");

        if (mCountThread != null) {
            mCountThread.interrupt();
            mCountThread = null;
            mCurNum = 0;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("countservice", "onBind");
        return mBinder;
    }



    @Override
    public void onRebind(Intent intent) {
        Log.i("countservice", "onRebind");
    }
}
