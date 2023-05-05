package hu.mobilalkkotprog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;

import hu.mobilalkkotprog.Meccs;
import hu.mobilalkkotprog.MeccsAdapter;
import hu.mobilalkkotprog.R;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList meccsArrayList;
    MeccsAdapter meccsAdapter;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_item1);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_item1) {
                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_item2) {
                Intent intent = new Intent(FavoritesActivity.this, AddMatchActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.navigation_item3) {
                Intent intent = new Intent(FavoritesActivity.this, ProfilActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        meccsArrayList = new ArrayList();
        meccsAdapter = new MeccsAdapter(FavoritesActivity.this, meccsArrayList);
        recyclerView.setAdapter(meccsAdapter);

        EventChangeListener();
    }

    private void EventChangeListener() {
        CollectionReference favoritesRef = db.collection("favorites");
        Query query = favoritesRef.whereEqualTo("userId", currentUserId);

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore error", error.getMessage());
                return;
            }

            meccsArrayList.clear();

            for (QueryDocumentSnapshot document : value) {
                String matchId = document.getString("matchId");
                getMatchData(matchId);
            }

            meccsAdapter.notifyDataSetChanged();
        });

    }

    private void getMatchData(String matchId) {
        CollectionReference matchesRef = db.collection("matches");

        matchesRef.document(matchId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Meccs meccs = document.toObject(Meccs.class);
                        meccs.setDocumentId(document.getId());
                        if (!meccsArrayList.contains(meccs)) {
                            meccsArrayList.add(meccs);
                        }
                        meccsAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("Firestore error", "Error getting match document: " + task.getException());
                }
            }
        });
    }
}
