package ch.hevs.aislab.intro.database.repository;

import android.content.Context;
import android.util.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import ch.hevs.aislab.intro.database.AppDatabase;
import ch.hevs.aislab.intro.database.async.CreateClient;
import ch.hevs.aislab.intro.database.async.DeleteClient;
import ch.hevs.aislab.intro.database.async.UpdateClient;
import ch.hevs.aislab.intro.database.entity.ClientEntity;
import ch.hevs.aislab.intro.util.OnAsyncEventListener;

public class ClientRepository {

    private static final String TAG = "ClientRepository";

    private static ClientRepository instance;

    private ClientRepository() {}

    public static ClientRepository getInstance() {
        if (instance == null) {
            synchronized (ClientRepository.class) {
                if (instance == null) {
                    instance = new ClientRepository();
                }
            }
        }
        return instance;
    }

    public LiveData<ClientEntity> getClient(final String email, Context context) {
        return AppDatabase.getInstance(context).clientDao().getByEmail(email);
    }

    public LiveData<List<ClientEntity>> getAllClients(Context context) {
        return AppDatabase.getInstance(context).clientDao().getAll();
    }

    public void insert(final ClientEntity client, OnAsyncEventListener callback, Context context) {
        new CreateClient(context, new OnAsyncEventListener() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
                Log.d(TAG, "createClient: success");
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
                Log.d(TAG, "createClient: failure", e);
            }
        }).execute(client);
    }

    public void update(final ClientEntity client, OnAsyncEventListener callback, Context context) {
        new UpdateClient(context, new OnAsyncEventListener() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
                Log.d(TAG, "updateClient: success");
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
                Log.d(TAG, "updateClient: failure", e);
            }
        }).execute(client);
    }

    public void delete(final ClientEntity client, OnAsyncEventListener callback, Context context) {
        new DeleteClient(context, new OnAsyncEventListener() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
                Log.d(TAG, "deleteClient: success");
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
                Log.d(TAG, "deleteClient: failure", e);
            }
        }).execute(client);
    }
}
