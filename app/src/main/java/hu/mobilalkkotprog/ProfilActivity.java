package hu.mobilalkkotprog;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private Button buttonLogout;



    private Button buttonModify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonModify = findViewById(R.id.buttonModify);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_item3);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_item1) {
                Intent intent = new Intent(ProfilActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_item2) {
                Intent intent = new Intent(ProfilActivity.this, AddMatchActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_item3) {
                Intent intent = new Intent(ProfilActivity.this, ProfilActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            Log.d(TAG, "Bejelentkezett felhasználó e-mail címe: " + email);
        }else {
            Log.d(TAG, "Nincs bejelentkezett felhasználó");
        }

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Log.d(TAG, "Felhasználó kijelentkezett");
                startActivity(new Intent(ProfilActivity.this, SignInActivity.class));
                finish();
            }
        });

        Button favoritesButton = findViewById(R.id.favoritesButton);
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilActivity.this, FavoritesActivity.class));
                finish();
            }
        });

        buttonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = editTextEmail.getText().toString().trim();
                String newPassword = editTextPassword.getText().toString().trim();

                if (newEmail.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(ProfilActivity.this, "Kérlek, töltsd ki az összes mezőt", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), newPassword);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        user.updateEmail(newEmail)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "E-mail cím sikeresen frissítve: " + newEmail);
                                                            Toast.makeText(ProfilActivity.this, "Email cím sikeresen frissítve. Új email: " + newEmail, Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(ProfilActivity.this, "Email cím frissítése sikertelen emiatt: : "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            Log.d(TAG, "E-mail cím frissítése sikertelen: " + task.getException().getMessage());
                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.d(TAG, "Reauthentikáció sikertelen: " + task.getException().getMessage());
                                    }
                                }
                            });
                }
            }
        });


    }




}
