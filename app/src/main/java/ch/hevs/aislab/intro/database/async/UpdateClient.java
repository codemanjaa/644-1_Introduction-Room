package ch.hevs.aislab.intro.database.async;

import android.app.Application;
import android.os.AsyncTask;

import ch.hevs.aislab.intro.database.entity.ClientEntity;
import ch.hevs.aislab.intro.database.repository.ClientRepository;
import ch.hevs.aislab.intro.util.OnAsyncEventListener;

public class UpdateClient extends AsyncTask<ClientEntity, Void, Void> {

    private static final String TAG = "UpdateClient";

    private Application mApplication;
    private OnAsyncEventListener mCallBack;
    private Exception mException;

    public UpdateClient(Application application, OnAsyncEventListener callback) {
        mApplication = application;
        mCallBack = callback;
    }

    @Override
    protected Void doInBackground(ClientEntity... params) {
        try {
            for (ClientEntity client : params)
                ClientRepository.getInstance(mApplication.getApplicationContext())
                        .update(client);
        } catch (Exception e) {
            mException = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess();
            } else {
                mCallBack.onFailure(mException);
            }
        }
    }
}