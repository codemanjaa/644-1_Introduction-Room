package ch.hevs.aislab.intro.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import ch.hevs.aislab.intro.R;
import ch.hevs.aislab.intro.database.async.CreateClient;
import ch.hevs.aislab.intro.database.entity.ClientEntity;
import ch.hevs.aislab.intro.util.OnAsyncEventListener;
import ch.hevs.aislab.intro.viewmodel.ClientViewModel;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ClientDetails extends AppCompatActivity {

    private static final String TAG = "ClientDetails";

    private static final int CREATE_CLIENT = 0;
    private static final int EDIT_CLIENT = 1;
    private static final int DELETE_CLIENT = 2;

    private Toast mToast;

    private boolean mEditable;

    private EditText mEtFirstName;
    private EditText mEtLastName;
    private EditText mEtEmail;

    private ClientViewModel mViewModel;

    private ClientEntity mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_client_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_activity_details);

        String clientEmail = getIntent().getStringExtra("clientEmail");

        initiateView();

        if (clientEmail != null) {
            ClientViewModel.Factory factory = new ClientViewModel.Factory(getApplication(), clientEmail);
            mViewModel = ViewModelProviders.of(this, factory).get(ClientViewModel.class);
            mViewModel.getClient().observe(this, clientEntity -> {
                if (clientEntity != null) {
                    mClient = clientEntity;
                    updateContent();
                }
            });
        } else {
            setTitle(R.string.title_activity_create);
            switchEditableMode();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (mClient != null)  {
            menu.add(0, EDIT_CLIENT, Menu.NONE, getString(R.string.action_edit))
                    .setIcon(R.drawable.ic_mode_edit_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.add(0, DELETE_CLIENT, Menu.NONE, getString(R.string.action_delete))
                    .setIcon(R.drawable.ic_delete_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            menu.add(0, CREATE_CLIENT, Menu.NONE, getString(R.string.action_create))
                    .setIcon(R.drawable.ic_add_white_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == EDIT_CLIENT) {
            if (mEditable) {
                item.setIcon(R.drawable.ic_mode_edit_white_24dp);
                switchEditableMode();
            } else {
                item.setIcon(R.drawable.ic_done_white_24dp);
                switchEditableMode();
            }
        }
        if (item.getItemId() == DELETE_CLIENT) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.action_delete));
            alertDialog.setCancelable(false);
            alertDialog.setMessage(getString(R.string.delete_msg));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.action_delete), (dialog, which) -> {
                mViewModel.deleteClient(mClient, new OnAsyncEventListener() {
                    @Override
                    public void onSuccess() {
                        onBackPressed();
                    }

                    @Override
                    public void onFailure(Exception e) {}
                });
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.action_cancel), (dialog, which) -> alertDialog.dismiss());
            alertDialog.show();
        }
        if (item.getItemId() == CREATE_CLIENT) {
            createClient(
                    mEtFirstName.getText().toString(),
                    mEtLastName.getText().toString(),
                    mEtEmail.getText().toString()
            );
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiateView() {
        mEditable = false;
        mEtFirstName = findViewById(R.id.firstName);
        mEtLastName = findViewById(R.id.lastName);
        mEtEmail = findViewById(R.id.email);

        mEtFirstName.setFocusable(false);
        mEtFirstName.setEnabled(false);
        mEtLastName.setFocusable(false);
        mEtLastName.setEnabled(false);
        mEtEmail.setFocusable(false);
        mEtEmail.setEnabled(false);
    }

    private void switchEditableMode() {
        if (!mEditable) {
            mEtFirstName.setFocusable(true);
            mEtFirstName.setEnabled(true);
            mEtLastName.setFocusable(true);
            mEtLastName.setEnabled(true);
            mEtEmail.setFocusable(true);
            mEtEmail.setEnabled(true);
            mEtEmail.setFocusableInTouchMode(true);
            mEtFirstName.requestFocus();
        } else {
            saveChanges(
                    mEtFirstName.getText().toString(),
                    mEtLastName.getText().toString(),
                    mEtEmail.getText().toString()
            );
            mEtFirstName.setFocusable(false);
            mEtFirstName.setEnabled(false);
            mEtLastName.setFocusable(false);
            mEtLastName.setEnabled(false);
            mEtEmail.setFocusable(false);
            mEtEmail.setEnabled(false);
        }
        mEditable = !mEditable;
    }

    private void createClient(String firstName, String lastName, String email) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEtEmail.setError(getString(R.string.error_invalid_email));
            mEtEmail.requestFocus();
            return;
        }

        mClient = new ClientEntity();
        mClient.setEmail(email);
        mClient.setFirstName(firstName);
        mClient.setLastName(lastName);
        new CreateClient(getApplication(), new OnAsyncEventListener() {
            @Override
            public void onSuccess() {
                onBackPressed();
            }

            @Override
            public void onFailure(Exception e) {}
        }).execute(mClient);
    }

    private void saveChanges(String firstName, String lastName, String email) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEtEmail.setError(getString(R.string.error_invalid_email));
            mEtEmail.requestFocus();
            return;
        }
        mClient.setEmail(email);
        mClient.setFirstName(firstName);
        mClient.setLastName(lastName);

        mViewModel.updateClient(mClient, new OnAsyncEventListener() {
            @Override
            public void onSuccess() {
                setResponse(true);
            }

            @Override
            public void onFailure(Exception e) {
                setResponse(false);
            }
        });
    }

    private void setResponse(Boolean response) {
        if (response) {
            updateContent();
            mToast = Toast.makeText(this, getString(R.string.client_edited), Toast.LENGTH_LONG);
            mToast.show();
        } else {
            mEtEmail.setError(getString(R.string.error_used_email));
            mEtEmail.requestFocus();
        }
    }

    private void updateContent() {
        if (mClient != null) {
            mEtFirstName.setText(mClient.getFirstName());
            mEtLastName.setText(mClient.getLastName());
            mEtEmail.setText(mClient.getEmail());
        }
    }
}
