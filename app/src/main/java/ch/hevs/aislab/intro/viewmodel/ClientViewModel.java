package ch.hevs.aislab.intro.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import ch.hevs.aislab.intro.database.async.DeleteClient;
import ch.hevs.aislab.intro.database.async.UpdateClient;
import ch.hevs.aislab.intro.database.entity.ClientEntity;
import ch.hevs.aislab.intro.database.repository.ClientRepository;
import ch.hevs.aislab.intro.util.OnAsyncEventListener;

public class ClientViewModel extends AndroidViewModel {

    private static final String TAG = "AccountViewModel";

    private ClientRepository mRepository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<ClientEntity> mObservableClient;

    public ClientViewModel(@NonNull Application application,
                           final String clientEmail, ClientRepository clientRepository) {
        super(application);

        mRepository = clientRepository;

        mObservableClient = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        mObservableClient.setValue(null);

        LiveData<ClientEntity> client = mRepository.getClient(clientEmail);

        // observe the changes of the client entity from the database and forward them
        mObservableClient.addSource(client, mObservableClient::setValue);
    }

    /**
     * A creator is used to inject the account id into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final String mClientEmail;

        private final ClientRepository mRepository;

        public Factory(@NonNull Application application, String clientEmail) {
            mApplication = application;
            mClientEmail = clientEmail;
            mRepository = ClientRepository.getInstance(application.getApplicationContext());
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ClientViewModel(mApplication, mClientEmail, mRepository);
        }
    }

    /**
     * Expose the LiveData ClientEntity query so the UI can observe it.
     */
    public LiveData<ClientEntity> getClient() {
        return mObservableClient;
    }

    public void updateClient(ClientEntity client, OnAsyncEventListener callback) {
        new UpdateClient(getApplication(), new OnAsyncEventListener() {
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

    public void deleteClient(ClientEntity client, OnAsyncEventListener callback) {
        new DeleteClient(getApplication(), new OnAsyncEventListener() {
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
