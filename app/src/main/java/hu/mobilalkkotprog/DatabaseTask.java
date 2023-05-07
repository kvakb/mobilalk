package hu.mobilalkkotprog;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import hu.mobilalkkotprog.Meccs;

public class DatabaseTask extends AsyncTask<Void, Void, Void> {



    private String homeTeam;
    private String awayTeam;
    private String homeGoals;
    private String awayGoals;
    private FirebaseFirestore db;

    public DatabaseTask(String homeTeam, String awayTeam, String homeGoals, String awayGoals) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        db = FirebaseFirestore.getInstance();
        Meccs match = new Meccs(homeTeam, awayTeam, homeGoals, awayGoals);

        CollectionReference matchesRef = db.collection("matches");

        matchesRef.add(match)
                .addOnSuccessListener(documentReference -> {
                    Log.d("DatabaseTask", "Mérkőzés sikeresen hozzáadva");

                })
                .addOnFailureListener(e -> {
                    Log.e("DatabaseTask", "Hiba történt a mérkőzés hozzáadása során", e);
                });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
