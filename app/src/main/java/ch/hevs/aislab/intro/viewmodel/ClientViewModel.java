package ch.hevs.aislab.intro.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import ch.hevs.aislab.intro.database.entity.ClientEntity;
import ch.hevs.aislab.intro.database.repository.ClientRepository;
import ch.hevs.aislab.intro.util.OnAsyncEventListener;

public class ClientViewModel extends AndroidViewModel {

    private ClientRepository repository;

    private Context applicationContext;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<ClientEntity> mObservableClient;

    public ClientViewModel(@NonNull Application application,
                           final String clientEmail, ClientRepository clientRepository) {
        super(application);

        repository = clientRepository;

        applicationContext = getApplication().getApplicationContext();

        mObservableClient = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        mObservableClient.setValue(null);

        LiveData<ClientEntity> client = repository.getClient(clientEmail, getApplication().getApplicationContext());

        // observe the changes of the client entity from the database and forward them
        mObservableClient.addSource(client, mObservableClient::setValue);
    }

    /**
     * A creator is used to inject the account id into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;

        private final String clientEmail;

        private final ClientRepository repository;

        public Factory(@NonNull Application application, String clientEmail) {
            this.application = application;
            this.clientEmail = clientEmail;
            repository = ClientRepository.getInstance();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ClientViewModel(application, clientEmail, repository);
        }
    }

    /**
     * Expose the LiveData ClientEntity query so the UI can observe it.
     */
    public LiveData<ClientEntity> getClient() {
        return mObservableClient;
    }

    public void createClient(ClientEntity client, OnAsyncEventListener callback) {
        repository.insert(client, callback, applicationContext);
    }

    public void updateClient(ClientEntity client, OnAsyncEventListener callback) {
        repository.update(client, callback, applicationContext);
    }

    public void deleteClient(ClientEntity client, OnAsyncEventListener callback) {
        repository.delete(client, callback, applicationContext);
    }
}
