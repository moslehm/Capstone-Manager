package ca.macewan.capstone.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import ca.macewan.capstone.R;

public class RecyclerAdapterV2 extends RecyclerView.Adapter<RecyclerAdapterV2.ViewHolder> {
    List<DocumentReference> documentReferenceList;
    OnProjectListener onProjectListener;

    public RecyclerAdapterV2(List<DocumentReference> documentReferenceList) {
        this.documentReferenceList = documentReferenceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);
        RecyclerAdapterV2.ViewHolder viewHolder = new RecyclerAdapterV2.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        documentReferenceList.get(position).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                holder.textView_pTitle.setText(task.getResult().get("name").toString());
                DocumentReference creatorRef = (DocumentReference) task.getResult().get("creator");
                creatorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            holder.textView_pCreator.setText(task.getResult().get("name").toString() +
                                    " <" + task.getResult().get("email").toString() + ">");
                        }
                    }
                });

                if (!task.getResult().getBoolean("status"))
                    holder.materialCardViewProject.setCardBackgroundColor(Color.parseColor("#fdaaaa"));
                else
                    holder.materialCardViewProject.setCardBackgroundColor(Color.parseColor("#77DD77"));
                }
                holder.viewProgressBarBackground.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void setOnProjectListener(RecyclerAdapterV2.OnProjectListener onProjectListener) {
        this.onProjectListener = onProjectListener;
    }

    @Override
    public int getItemCount() {
        return documentReferenceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView_pCreator, textView_pTitle;
        View viewProgressBarBackground;
        ProgressBar progressBar;
        MaterialCardView materialCardViewProject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView_pCreator = itemView.findViewById(R.id.textView_pCreator);
            this.textView_pTitle = itemView.findViewById(R.id.textView_pTitle);
            this.viewProgressBarBackground = itemView.findViewById(R.id.viewProgressBarBackground);
            this.progressBar = itemView.findViewById(R.id.progressBar);
            materialCardViewProject = itemView.findViewById(R.id.materialCardView_Project);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProjectListener.onProjectClick(getBindingAdapterPosition(),
                    documentReferenceList.get(getBindingAdapterPosition()).getId());
        }
    }

    public interface OnProjectListener {
        void onProjectClick(int position, String projectID);
    }
}
