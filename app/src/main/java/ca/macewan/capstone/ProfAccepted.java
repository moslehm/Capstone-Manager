package ca.macewan.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import ca.macewan.capstone.adapter.RecyclerAdapterV2;

public class ProfAccepted extends Fragment {
    private RecyclerView recyclerView_Accepted;
    private FirebaseFirestore db;
    private RecyclerAdapterV2 recyclerAdapterV2;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_accepted, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView_Accepted = (RecyclerView) requireView().findViewById(R.id.recyclerView_Accepted);
        db = FirebaseFirestore.getInstance();
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    refresh();
                }

            }
        });
        db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            recyclerAdapterV2 = new RecyclerAdapterV2((List<DocumentReference>) task.getResult().get("accepted"));
                            recyclerView_Accepted.setAdapter(recyclerAdapterV2);
                            recyclerView_Accepted.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerAdapterV2.setOnProjectListener(new RecyclerAdapterV2.OnProjectListener() {
                                @Override
                                public void onProjectClick(int position, String projectID) {
                                    Intent intent = new Intent(getContext(), ProjectInfoActivityProf.class);
                                    intent.putExtra("projectID", projectID);
                                    activityResultLauncher.launch(intent);
                                }
                            });
                        }
                    }
                });
    }

     private void refresh() {
        db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            recyclerAdapterV2 = new RecyclerAdapterV2((List<DocumentReference>) task.getResult().get("accepted"));
                            recyclerView_Accepted.setAdapter(recyclerAdapterV2);
                            recyclerAdapterV2.setOnProjectListener(new RecyclerAdapterV2.OnProjectListener() {
                                @Override
                                public void onProjectClick(int position, String projectID) {
                                    Intent intent = new Intent(getContext(), ProjectInfoActivityProf.class);
                                    intent.putExtra("projectID", projectID);
                                    activityResultLauncher.launch(intent);
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
