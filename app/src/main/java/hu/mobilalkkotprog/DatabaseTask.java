package hu.mobilalkkotprog;

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
        // Firebase Firestore hivatkozás inicializálása
        db = FirebaseFirestore.getInstance();

        // Adatobjektum létrehozása
        Meccs match = new Meccs(homeTeam, awayTeam, homeGoals, awayGoals);

        // Firestore gyűjtemény hivatkozás létrehozása
        CollectionReference matchesRef = db.collection("matches");

        // Adatobjektum beszúrása a Firestore adatbázisba
        matchesRef.add(match)
                .addOnSuccessListener(documentReference -> {
                    // Sikeres beszúrás esetén
                    Log.d("DatabaseTask", "Mérkőzés sikeresen hozzáadva");
                })
                .addOnFailureListener(e -> {
                    // Hiba esetén
                    Log.e("DatabaseTask", "Hiba történt a mérkőzés hozzáadása során", e);
                });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // Itt kezelheted az adatbázisművelet utáni teendőket
        // Például frissítheted a felhasználói felületet vagy megjeleníthetsz egy Toast üzenetet
    }
}
