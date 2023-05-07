package hu.mobilalkkotprog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {
    private String modositandoID;
    private EditText editHomeTeam;
    private EditText editAwayTeam;
    private EditText editHomeScore;
    private EditText editAwayScore;
    private Button btnSave;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity);

        db = FirebaseFirestore.getInstance();

        Intent intents = getIntent();

        editHomeTeam = findViewById(R.id.editHomeTeam);
        editAwayTeam = findViewById(R.id.editAwayTeam);
        editHomeScore = findViewById(R.id.editHomeScore);
        editAwayScore = findViewById(R.id.editAwayScore);
        btnSave = findViewById(R.id.btnSave);

        editHomeTeam.setText(intents.getStringExtra("homeTeam"));
        editAwayTeam.setText(intents.getStringExtra("awayTeam"));
        editHomeScore.setText(intents.getStringExtra("homeScore"));
        editAwayScore.setText(intents.getStringExtra("awayScore"));
        modositandoID = intents.getStringExtra("meccsId");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_item1);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_item1) {
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_item2) {
                Intent intent = new Intent(UpdateActivity.this, AddMatchActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_item3) {
                Intent intent = new Intent(UpdateActivity.this, ProfilActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

    }

    private void updateData() {
        String homeTeam = editHomeTeam.getText().toString();
        String awayTeam = editAwayTeam.getText().toString();
        String homeScore = editHomeScore.getText().toString();
        String awayScore = editAwayScore.getText().toString();


        DocumentReference docRef = db.collection("matches").document(modositandoID);
        docRef.update("home_team", homeTeam,
                        "away_team", awayTeam,
                        "home_score", homeScore,
                        "away_score", awayScore)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateActivity.this, "Frissítés sikeres", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateActivity.this, "Frissítés sikertelen", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
