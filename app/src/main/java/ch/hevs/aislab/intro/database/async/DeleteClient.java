package ch.hevs.aislab.intro.database.async;

import android.content.Context;
import android.os.AsyncTask;

import ch.hevs.aislab.intro.database.AppDatabase;
import ch.hevs.aislab.intro.database.entity.ClientEntity;
import ch.hevs.aislab.intro.util.OnAsyncEventListener;

public class DeleteClient extends AsyncTask<ClientEntity, Void, Void> {

    private AppDatabase mDatabase;
    private OnAsyncEventListener mCallBack;
    private Exception mException;

    public DeleteClient(Context context, OnAsyncEventListener callback) {
        mDatabase = AppDatabase.getInstance(context);
        mCallBack = callback;
    }

    @Override
    protected Void doInBackground(ClientEntity... params) {
        try {
            for (ClientEntity client : params)
                mDatabase.clientDao().delete(client);
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