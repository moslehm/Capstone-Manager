package ca.macewan.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.macewan.capstone.adapter.SharedMethods;
import uk.co.onemandan.materialtextview.MaterialTextView;

public class ProjectInfoActivityProf extends AppCompatActivity {
    Button button_Accept, button_Decline;
    FirebaseFirestore db;
    String projectID;
//    MaterialTextView materialTextView_Creator, materialTextView_Title, materialTextView_Supervisor,
//            materialTextView_Members, materialTextView_Description;
//    CheckBox checkBox_Status;
    String email;
    private DocumentReference projectRef;
    private DocumentReference userRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info_prof);
        db = FirebaseFirestore.getInstance();
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        setUp();
        refreshButtons();
    }

    private void setUp() {
        button_Accept = findViewById(R.id.button_Accept);
        button_Decline = findViewById(R.id.button_Decline);

        projectID = getIntent().getExtras().getString("projectID");
        projectRef = db.collection("Projects").document(projectID);
        userRef = db.collection("Users").document(email);
        View projectView = findViewById(R.id.projectLayout);
        SharedMethods.setupProjectView(projectView, projectRef, email, this);

        button_Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(ProjectInfoActivityProf.this)
                        .setMessage("Decline the request?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // remove the invitation from array
                                userRef.update("invited", FieldValue.arrayRemove(projectRef.getId()));
                                projectRef.update("supervisorsPending", userRef);
                                refreshButtons();
                            }
                        })
                        .show();
            }
        });

        button_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(ProjectInfoActivityProf.this)
                        .setMessage("Agree to supervise?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // remove supervisor from pending supervisors list
                                projectRef.update("supervisorsPending", FieldValue.arrayRemove(userRef));
                                // add prof to supervisor list
                                projectRef.update("supervisors", FieldValue.arrayUnion(userRef));
                                // add request to accepted array
                                userRef.update("projects", FieldValue.arrayUnion(projectRef.getId()));
                                // remove request from invited array
                                userRef.update("invited", FieldValue.arrayRemove(projectRef.getId()));
                                refreshButtons();
                            }
                        })
                        .show();
            }
        });
    }

    private void refreshButtons() {
        projectRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    boolean status = documentSnapshot.getBoolean("status");
                    List<DocumentReference> supervisorList = (ArrayList<DocumentReference>) documentSnapshot.get("supervisors");
                    buttonAcceptSetUp(status, supervisorList);
                    buttonDeclineSetup(status, supervisorList);
                }
            }
        });
    }

    private void buttonDeclineSetup(boolean status, List<DocumentReference> supervisorList) {
        // won't be able to decline invitation after project has closed
        if (!status)
            button_Decline.setEnabled(false);
        // already accepted the invitation
        if (supervisorList != null && supervisorList.contains(userRef)) {
            button_Decline.setEnabled(false);
        }

        // disable the button for prof that was not invited to supervise
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> invitedProjList = (List<String>) task.getResult().get("invited");
                    if (!invitedProjList.contains(projectRef.getId())) {
                        button_Decline.setEnabled(false);
                    }
                }
            }
        });
    }

    private void buttonAcceptSetUp(boolean status, List<DocumentReference> supervisorList) {
        // won't be able to accept invitation after project has closed
        if (!status)
            button_Accept.setEnabled(false);
        // already accepted the invitation
        if (supervisorList != null && supervisorList.contains(userRef)) {
            button_Accept.setEnabled(false);
        }
        // disable the button for prof that was not invited to supervisor
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> invitedProjList = (List<String>) task.getResult().get("invited");
                    if (!invitedProjList.contains(projectRef.getId())) {
                        button_Accept.setEnabled(false);
                    }
                }
            }
        });
    }
}