package ch.hevs.aislab.intro.database.repository;

import android.content.Context;

import java.util.List;

import androidx.lifecycle.LiveData;
import ch.hevs.aislab.intro.database.AppDatabase;
import ch.hevs.aislab.intro.database.entity.ClientEntity;

public class ClientRepository {

    private static ClientRepository sInstance;

    private final AppDatabase mDatabase;

    private ClientRepository(final Context context) {
        mDatabase = AppDatabase.getInstance(context);
    }

    public static ClientRepository getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (ClientRepository.class) {
                if (sInstance == null) {
                    sInstance = new ClientRepository(context);
                }
            }
        }
        return sInstance;
    }

    public LiveData<ClientEntity> getClient(final String email) {
        return mDatabase.clientDao().getByEmail(email);
    }

    public LiveData<List<ClientEntity>> getAllClients() {
        return mDatabase.clientDao().getAll();
    }

    public void insert(final ClientEntity client) {
        mDatabase.clientDao().insert(client);
    }

    public void update(final ClientEntity client) {
        mDatabase.clientDao().update(client);
    }

    public void delete(final ClientEntity client) {
        mDatabase.clientDao().delete(client);
    }
}
