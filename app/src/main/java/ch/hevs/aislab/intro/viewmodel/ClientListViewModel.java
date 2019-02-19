package ch.hevs.aislab.intro.viewmodel;

import android.app.Application;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import ch.hevs.aislab.intro.database.entity.ClientEntity;
import ch.hevs.aislab.intro.database.repository.ClientRepository;
import ch.hevs.aislab.intro.util.OnAsyncEventListener;

public class ClientListViewModel extends AndroidViewModel {

    private static final String TAG = "ClientListViewModel";

    private ClientRepository mRepository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<ClientEntity>> mObservableClients;

    public ClientListViewModel(@NonNull Application application, ClientRepository clientRepository) {
        super(application);

        mRepository = clientRepository;

        mObservableClients = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        mObservableClients.setValue(null);

        LiveData<List<ClientEntity>> clients = mRepository.getAllClients(getApplication().getApplicationContext());

        // observe the changes of the entities from the database and forward them
        mObservableClients.addSource(clients, mObservableClients::setValue);
    }

    /**
     * A creator is used to inject the account id into the ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final ClientRepository mClientRepository;

        public Factory(@NonNull Application application) {
            mApplication = application;
            mClientRepository = ClientRepository.getInstance();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ClientListViewModel(mApplication, mClientRepository);
        }
    }

    /**
     * Expose the LiveData ClientEntities query so the UI can observe it.
     */
    public LiveData<List<ClientEntity>> getClients() {
        return mObservableClients;
    }

    public void deleteClient(ClientEntity client) {
        mRepository.delete(client, new OnAsyncEventListener() {
            @Override
            public void onSuccess() { }

            @Override
            public void onFailure(Exception e) { }
        }, getApplication().getApplicationContext());
    }
}
