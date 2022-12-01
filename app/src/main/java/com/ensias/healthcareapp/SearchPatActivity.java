package com.ensias.healthcareapp;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.ensias.healthcareapp.model.Doctor;
import com.ensias.healthcareapp.utils.HCAlert;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchPatActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference doctorRef = db.collection("Doctor");

    private DoctorAdapterFiltred adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_pat);
        configureToolbar();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        AlertDialog progress = HCAlert.showProgress(this, "Fetching Doctor's List...");

        RecyclerView recyclerView = findViewById(R.id.serachPatRecycle);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = doctorRef.orderBy("name", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(task -> {
            List<String> avoidList = Arrays.asList("เดช", "สุรเดช เชียงทับ", "ดำ","Мамай Жанель","Бейбарыс","z","yes","yash chutiya ","xyz", "xx22","which the","vk","vinh mai","uwiw","tt","test ","tester", "tes", "test", "temp@gmail.com", "ss","sa","rr","no","ntate","okcheck","n","me hu","maja", "kwnil","j","hdhdh","gsjsj","gmam","fjfjfjf","fff","fa","ee","drd","dr","doctorAPP","doctor123","doctor try","dimplekk","dhdxgj", "ddd","dctr", "data","d12345t","d","crcfrc","cofee","ash","ari","ar","amr","al","abs2", "abs","abc","abby","aaaaa","aa","Yu", "We","Võ Trương Hải Đăng", "TestDoctor", "", "Doctir","As","AAAA","1234567");
            List<Doctor> doctors = task.getResult().toObjects(Doctor.class);
            ArrayList<Doctor> filteredDoctors = new ArrayList<>();
            HashMap<String, String> duplicates = new HashMap<>();
            for(Doctor doctor: doctors) {
                if (!duplicates.containsKey(doctor.getName().toLowerCase())) {
                    duplicates.put(doctor.getName().toLowerCase(), doctor.getSpecialite());
                }else {
                    String specialisation = duplicates.get(doctor.getName().toLowerCase());
                    if (doctor.getSpecialite().equalsIgnoreCase(specialisation)) {
                        continue;
                    }
                }
                if (!avoidList.contains(doctor.getName())) {
                    filteredDoctors.add(doctor);
                }
            }

            Collections.sort(filteredDoctors, (t1, t2) -> t1.getName().compareTo(t2.getName()));

//            String allDoctors = "";
//            for (Doctor doctor: filteredDoctors) {
//                allDoctors += "\""+doctor.getName()+"\",";
//            }
//            Log.d("SearchPatActivity", "all doctors data: "+(allDoctors));
            adapter = new DoctorAdapterFiltred(filteredDoctors);
            recyclerView.setAdapter(adapter);
            progress.dismiss();
        });
        //FirestoreRecyclerOptions<Doctor> options = new FirestoreRecyclerOptions.Builder<Doctor>()
              //  .setQuery(query, Doctor.class)
              //  .build();

        //adapter = new DoctoreAdapter(options);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        Drawable r= getResources().getDrawable(R.drawable.ic_local_hospital_black_24dp);
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" Specialite" );
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //menu.findItem(R.id.empty).setTitle(sb);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint(Html.fromHtml("<font color = #000000>" + "Search patient" + "</font>"));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchTimer != null) searchTimer.cancel();
                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d("SearchActivity", "searching for query: "+ newText);
                        adapter.filterBasedOnSearch(newText);
                    }
                }, 500);
                return false;
            }
        });
        return true;
    }

    private Timer searchTimer;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //3 - Handle actions on menu items
        switch (item.getItemId()) {
            case R.id.option_all:
                adapter.filterBasedOnMenu("");
                return true;
            case R.id.option_general:
                adapter.filterBasedOnMenu("Médecin général");
                return true;
            case R.id.option_Dentiste:
                adapter.filterBasedOnMenu("Dentiste");
                return true;
            case R.id.option_Ophtalmologue:
                adapter.filterBasedOnMenu("Ophtalmologue");
                return true;
            case R.id.option_ORL:
                adapter.filterBasedOnMenu("ORL");
                return true;
            case R.id.option_Pédiatre:
                adapter.filterBasedOnMenu("Pédiatre");
                return true;
            case R.id.option_Radiologue:
                adapter.filterBasedOnMenu("Radiologue");
                return true;
            case R.id.option_Rhumatologue:
                adapter.filterBasedOnMenu("Rhumatologue");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void configureToolbar(){
        // Get the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        toolbar.setTitle("Doctors list");
        // Sets the Toolbar
        setSupportActionBar(toolbar);
    }


}
