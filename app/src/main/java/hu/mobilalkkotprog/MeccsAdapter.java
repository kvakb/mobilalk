package hu.mobilalkkotprog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MeccsAdapter extends RecyclerView.Adapter<MeccsAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Meccs> meccsArrayList;

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
        return new MyViewHolder(v, deleteIcon);
    }

    @Override
    public void onBindViewHolder(@NonNull MeccsAdapter.MyViewHolder holder, int position) {
        Meccs meccs = meccsArrayList.get(position);
        holder.homeTeam.setText(meccs.getHome_team());
        holder.homeScore.setText(meccs.getHome_score());
        holder.awayTeam.setText(meccs.getAway_team());
        holder.awayScore.setText(meccs.getAway_score());
    }

    @Override
    public int getItemCount() {
        return meccsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteIcon;
        private TextView homeTeam, awayTeam, homeScore, awayScore;

        public MyViewHolder(@NonNull View itemView, ImageView deleteIcon) {
            super(itemView);
            this.deleteIcon = deleteIcon;
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
    }
}
