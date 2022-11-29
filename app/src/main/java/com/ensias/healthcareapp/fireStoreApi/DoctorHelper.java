package com.ensias.healthcareapp.fireStoreApi;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ensias.healthcareapp.DoctorAdapterFiltred;
import com.ensias.healthcareapp.Interface.DoctorDataListener;
import com.ensias.healthcareapp.model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class DoctorHelper {
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference DoctorRef = db.collection("Doctor");

    private static final String TAG = "DoctorHelper";

    public static void addDoctor(String name, String adresse, String tel,String specialite){
        Doctor doctor = new Doctor(name,adresse,tel, FirebaseAuth.getInstance().getCurrentUser().getEmail(),specialite);
        DoctorRef.document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(doctor);
    }

    public static Void getDoctorById(String name, DoctorDataListener listener) {
        Query query = DoctorRef.whereEqualTo("name", name);

        query.get().addOnCompleteListener(task -> {
            if (task.getResult() != null) {
                List<Doctor> doctors = task.getResult().toObjects(Doctor.class);
                if (doctors.size() != 0) {
                    listener.getDoctor(doctors.get(0));
                    return;
                }
            }
            listener.getDoctor(null);
        });
        return null;
    }

    public static String toEnglish(String fromFrench) {
        switch (fromFrench.toLowerCase()) {
            case "général":
                return "General";
            case "médecin général":
                return "General physician";
            case "dentiste":
                return "Dentist";
            case "ophtalmologue":
                return "Ophthalmologist";
            case "orl":
                return "ENT";
            case "pédiatre":
                return "Pediatrician";
            case "radiologue":
                return "Radiologist";
            case "rhumatologue":
                return "Rheumatologist";
            default:
                Log.d(TAG, "Unable to convert specialisation: "+fromFrench);
                return fromFrench;
        }
    }

    public static String toFrench(String fromEnglish) {
        switch (fromEnglish.toLowerCase()) {
            case "general physician":
                return "Médecin général";
            case "dentist":
                return  "Dentiste";
            case "ophthalmologist":
                return "Ophtalmologue";
            case "ent":
                return "ORL";
            case "pediatrician":
                return "Pédiatre";
            case "radiologist":
                return "Radiologue";
            case "rheumatologist":
                return "Rhumatologue";
            default:
                return fromEnglish;
        }
    }
}
