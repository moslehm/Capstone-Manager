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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Locale;

import ca.macewan.capstone.adapter.RecyclerAdapter;
import ca.macewan.capstone.adapter.RecyclerAdapterV2;

public class ProfInvited extends Fragment implements RecyclerAdapter.OnProjectListener {
    private RecyclerView recyclerViewProject;
    private FirebaseFirestore db;
//    private RecyclerAdapterV2 recyclerAdapterV2;
    private RecyclerAdapter recyclerAdapter;
    ActivityResultLauncher<Intent> activityResultLauncher;
    String email;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_invited, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewProject = (RecyclerView) requireView().findViewById(R.id.recyclerView_Invited);
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db = FirebaseFirestore.getInstance();
        setUp();
    }

    public void setUp() {
        Query query = FirebaseFirestore.getInstance().collection("Projects")
                .whereArrayContains("supervisorsPending",
                        db.document("Users/" + email));
        FirestoreRecyclerOptions<Project> options = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class)
                .build();
        recyclerAdapter = new RecyclerAdapter(options);
        recyclerViewProject.setAdapter(recyclerAdapter);
        recyclerViewProject.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter.setOnProjectListener(ProfInvited.this);
        recyclerViewProject.setItemAnimator(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }

    @Override
    public void onProjectClick(int position, String projectID) {
        Intent intent = new Intent(getContext(), ProjectInfoActivityProf.class);
        intent.putExtra("projectID", projectID);
        startActivity(intent);
    }
}


//        db = FirebaseFirestore.getInstance();
//        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//            @Override
//            public void onActivityResult(ActivityResult result) {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    refresh();
//                }
//
//            }
//        });
//        db.collection("Users")
//                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            recyclerAdapterV2 = new RecyclerAdapterV2((List<DocumentReference>) task.getResult().get("invited"));
//                            recyclerView_Invited.setAdapter(recyclerAdapterV2);
//                            recyclerView_Invited.setLayoutManager(new LinearLayoutManager(getActivity()));
//                            recyclerAdapterV2.setOnProjectListener(new RecyclerAdapterV2.OnProjectListener() {
//                                @Override
//                                public void onProjectClick(int position, String projectID) {
//                                    Intent intent = new Intent(getContext(), ProjectInfoActivityProf.class);
//                                    intent.putExtra("projectID", projectID);
//                                    activityResultLauncher.launch(intent);
//                                }
//                            });
//                        }
//                    }
//                });
//    }
//
//    private void refresh() {
//        db.collection("Users")
//                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString())
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            recyclerAdapterV2 = new RecyclerAdapterV2((List<DocumentReference>) task.getResult().get("invited"));
//                            recyclerView_Invited.setAdapter(recyclerAdapterV2);
//                            recyclerAdapterV2.setOnProjectListener(new RecyclerAdapterV2.OnProjectListener() {
//                                @Override
//                                public void onProjectClick(int position, String projectID) {
//                                    Intent intent = new Intent(getContext(), ProjectInfoActivityProf.class);
//                                    intent.putExtra("projectID", projectID);
//                                    activityResultLauncher.launch(intent);
//                                }
//                            });
//                        }
//                    }
//                });
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        refresh();
//    }
//}