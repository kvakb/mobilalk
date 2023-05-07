package hu.mobilalkkotprog;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MeccsAdapter extends RecyclerView.Adapter<MeccsAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Meccs> meccsArrayList;

    private FirebaseAuth mAuth;


    private FirebaseFirestore db;


    public MeccsAdapter(Context context, ArrayList<Meccs> meccsArrayList) {
        this.context = context;
        this.meccsArrayList = meccsArrayList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MeccsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_meccs, parent, false);
        ImageView deleteIcon = v.findViewById(R.id.deleteIcon);
        ImageView favoriteIcon = v.findViewById(R.id.favoriteIcon);
        ImageView editIcon = v.findViewById(R.id.editIcon);

        return new MyViewHolder(v, deleteIcon, favoriteIcon, editIcon);
    }

    @Override
    public void onBindViewHolder(@NonNull MeccsAdapter.MyViewHolder holder, int position) {
        Meccs meccs = meccsArrayList.get(position);
        holder.homeTeam.setText(meccs.getHome_team());
        holder.homeScore.setText(meccs.getHome_score());
        holder.awayTeam.setText(meccs.getAway_team());
        holder.awayScore.setText(meccs.getAway_score());

        String matchId = meccs.getDocumentId();
        isMatchFavorite(matchId, holder.favoriteIcon);
    }
    private void isMatchFavorite(String matchId, ImageView favoriteIcon) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        CollectionReference favoritesCollectionRef = db.collection("favorites");
        Query query = favoritesCollectionRef.whereEqualTo("matchId", matchId).whereEqualTo("userId", userId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean isFavorite = !task.getResult().isEmpty();
                    if (isFavorite) {
                        favoriteIcon.setImageResource(R.drawable.ic_favorite_filled);
                    } else {
                        favoriteIcon.setImageResource(R.drawable.favorite);
                    }
                } else {
                    Log.e(TAG, "Hiba a kedvenc meccsek lekérdezésekor", task.getException());
                    Toast.makeText(context, "Hiba történt a kedvenc meccsek lekérdezésekor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return meccsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView favoriteIcon;
        private ImageView editIcon;

        private ImageView deleteIcon;
        private TextView homeTeam, awayTeam, homeScore, awayScore;


        public MyViewHolder(@NonNull View itemView, ImageView deleteIcon, ImageView favoriteIcon, ImageView editIcon ) {
            super(itemView);
            this.deleteIcon = deleteIcon;
            this.favoriteIcon = favoriteIcon;
            this.editIcon = editIcon;
            homeTeam = itemView.findViewById(R.id.homeTeam);
            awayTeam = itemView.findViewById(R.id.awayTeam);
            homeScore = itemView.findViewById(R.id.homeScore);
            awayScore = itemView.findViewById(R.id.awayScore);




            deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deleteItem(position);
                    }
                }
            });
            favoriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        addFavorites(position);
                    }
                }
            });
            editIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Meccs meccs = meccsArrayList.get(position);
                        Intent intent = new Intent(context, UpdateActivity.class);
                        intent.putExtra("meccsId", meccs.getDocumentId());
                        intent.putExtra("homeTeam", meccs.getHome_team());
                        intent.putExtra("awayTeam", meccs.getAway_team());
                        intent.putExtra("homeScore", meccs.getHome_score());
                        intent.putExtra("awayScore", meccs.getAway_score());

                        context.startActivity(intent);
                    }
                }
            });
        }

        private void deleteItem(int position) {
            String documentId = meccsArrayList.get(position).getDocumentId();
            CollectionReference collectionRef = db.collection("matches");
            collectionRef.document(documentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Törlés sikeres", Toast.LENGTH_SHORT).show();
                            deleteMatchFromFavorites(documentId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Törlés sikertelen", Toast.LENGTH_SHORT).show();
                        }
                    });

            meccsArrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, meccsArrayList.size());
        }
        private void deleteMatchFromFavorites(String matchId) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String userId = mAuth.getCurrentUser().getUid();

            CollectionReference favoritesCollectionRef = db.collection("favorites");
            Query query = favoritesCollectionRef.whereEqualTo("userId", userId)
                    .whereEqualTo("matchId", matchId);
            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete();
                                }
                            } else {
                                Log.e(TAG, "Hiba a favorites gyűjtemény lekérdezésekor", task.getException());
                                Toast.makeText(context, "Hiba történt a favorites gyűjtemény lekérdezésekor", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        private void addFavorites(int position) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String userId = mAuth.getCurrentUser().getUid();

            String matchId = meccsArrayList.get(position).getDocumentId();

            db.collection("favorites")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("matchId", matchId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    Map<String, Object> favoriteMap = new HashMap<>();
                                    favoriteMap.put("userId", userId);
                                    favoriteMap.put("matchId", matchId);
                                    db.collection("favorites")
                                            .add(favoriteMap)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(context, "Meccs hozzáadva a kedvencekhez", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e(TAG, "Hiba a meccs hozzáadásakor", e);
                                                    Toast.makeText(context, "Hiba történt a meccs hozzáadásakor", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    favoriteIcon.setImageResource(R.drawable.ic_favorite_filled);
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        document.getReference().delete();
                                    }
                                    Toast.makeText(context, "Meccs eltávolítva a kedvencekből", Toast.LENGTH_SHORT).show();
                                    favoriteIcon.setImageResource(R.drawable.favorite);
                                }
                            } else {
                                Log.e(TAG, "Hiba a kedvenc meccsek lekérdezésekor", task.getException());
                                Toast.makeText(context, "Hiba történt a kedvenc meccsek lekérdezésekor", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }
}
