package hu.mobilalkkotprog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddMatchActivity extends AppCompatActivity {

    private EditText editTextHomeTeam;
    private EditText editTextAwayTeam;
    private EditText editTextHomeGoals;
    private EditText editTextAwayGoals;
    private Button buttonSubmit;

    private FirebaseFirestore db;
    private Notification mNotificationConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmatch);
        mNotificationConfig = new Notification(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_item2);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_item1) {
                Intent intent = new Intent(AddMatchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_item2) {
                Intent intent = new Intent(AddMatchActivity.this, AddMatchActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_item3) {
                Intent intent = new Intent(AddMatchActivity.this, ProfilActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        db = FirebaseFirestore.getInstance();

        editTextHomeTeam = findViewById(R.id.editTextHomeTeam);
        editTextAwayTeam = findViewById(R.id.editTextAwayTeam);
        editTextHomeGoals = findViewById(R.id.editTextHomeGoals);
        editTextAwayGoals = findViewById(R.id.editTextAwayGoals);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String homeTeam = editTextHomeTeam.getText().toString().trim();
                String awayTeam = editTextAwayTeam.getText().toString().trim();
                String homeGoals = editTextHomeGoals.getText().toString().trim();
                String awayGoals = editTextAwayGoals.getText().toString().trim();

                if (homeTeam.isEmpty() || awayTeam.isEmpty() || homeGoals.isEmpty() || awayGoals.isEmpty()) {
                    Toast.makeText(AddMatchActivity.this, "Kérlek, töltsd ki az összes mezőt", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseTask task = new DatabaseTask(homeTeam, awayTeam, homeGoals, awayGoals);
                task.execute();
                mNotificationConfig.send("Mérkőzés sikeresen hozzáadva!");

                Intent intent = new Intent(AddMatchActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
