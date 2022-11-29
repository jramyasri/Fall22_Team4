package com.ensias.healthcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ensias.healthcareapp.model.User;
import com.ensias.healthcareapp.utils.HCAlert;
import com.ensias.healthcareapp.utils.HCUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private Button signUpBtn;
    private EditText emailText;
    private EditText passwordText;
    private Button loginBtn;
    private Button creatBtn;
    private EditText secondPass;
    private EditText confirme;
    SignInButton signInButton;
    FirebaseFirestore  db = FirebaseFirestore.getInstance();
    private CollectionReference UsersRef = db.collection("User");
    public static final String TAG = "MainActivity";

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
       mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        confirme = (EditText)findViewById(R.id.editText3);
        confirme.setVisibility(View.INVISIBLE);
        signInButton = findViewById(R.id.sign_in_button);

//        TextView textView = (TextView) signInButton.getChildAt(0);

        emailText= (EditText) findViewById(R.id.editText2);
        passwordText= (EditText) findViewById(R.id.editText);
        secondPass= (EditText) findViewById(R.id.editText3);
        signUpBtn =(Button)findViewById(R.id.SignUpBtn);
        loginBtn = (Button)findViewById(R.id.LoginBtn);
        creatBtn = findViewById(R.id.CreateAccount);
        signUpBtn.setVisibility(View.GONE);

        signUpBtn.setOnClickListener(v -> {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            if (canProceedSigningUp()) {
                proceedSignup(email, password);
            }
        });

        loginBtn.setOnClickListener(v -> {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            if (canProceedLogin()) {
                proceedLogin(email, password);
            }
        });

        creatBtn.setOnClickListener(v -> {
            emailText.setText("");
            passwordText.setText("");
            confirme.setText("");
            if (creatBtn.getText().toString().equals("Create Account")){
                confirme.setVisibility(View.VISIBLE);
                signUpBtn.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.INVISIBLE);
                creatBtn.setText("Back to login");
                signInButton.setVisibility(View.GONE);
            }
            else{
                confirme.setVisibility(View.INVISIBLE);
                signUpBtn.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                creatBtn.setText("Create Account");
                signInButton.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.sign_in_button).setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent,RC_SIGN_IN);
        });
    }

    private void proceedLogin(String email, String password) {

        Activity context = this;
        AlertDialog dialog = HCAlert.showProgress(this, "Logging In....\nPlease wait");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        String message = "Please check you credentials and try again";
                        if (task.getException() != null && task.getException().getLocalizedMessage() != null) message = task.getException().getLocalizedMessage();
                        HCAlert.showAlert(context, "Invalid Credentials", message);
                        updateUI(null);
                    }
                });
    }

    private boolean canProceedLogin() {
        String email=emailText.getText().toString();
        String password=passwordText.getText().toString();

        if (HCUtils.isValidEmail(email)) {
            if (!password.isEmpty()) {
                return true;
            }else {
                HCAlert.showAlert(getActivityContext(), "Alert", "Password should not be Empty");
            }
        }else {
            HCAlert.showAlert(getActivityContext(), "Alert", "Please enter a valid Email-Id");
        }
        return false;
    }

    private boolean canProceedSigningUp() {
        String email=emailText.getText().toString();
        String password=passwordText.getText().toString();
        String confirmPass = secondPass.getText().toString();

        if (HCUtils.isValidEmail(email)) {
            if (!password.isEmpty()) {
                if (password.equals(confirmPass)) {
                    return true;
                }else {
                    HCAlert.showAlert(getActivityContext(), "Alert", "Password and confirm password should match");
                }
            }else {
                HCAlert.showAlert(getActivityContext(), "Alert", "Password should not be Empty");
            }
        }else {
            HCAlert.showAlert(getActivityContext(), "Alert", "Please enter a valid Email-Id");
        }
        return false;
    }

    private Activity getActivityContext() {
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void proceedSignup(String email, String password) {
        Activity context = this;
        AlertDialog dialog = HCAlert.showProgress(this, "Signing up....\nPlease wait");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure" + task.getException().getLocalizedMessage());

                            String message = "Something went wrong, please try after some time or use different Email";
                            if (task.getException().getLocalizedMessage() != null)
                                message = task.getException().getLocalizedMessage();

                            HCAlert.showAlert(context, "Alert", message, () -> { });
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ..
                    }
                });
    }
    private void updateUI(final FirebaseUser currentUser) {
        if(currentUser!=null){
            try {
                UsersRef.document(currentUser.getEmail()).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UsersRef.document(currentUser.getEmail()).get().addOnSuccessListener(documentSnapshot1 -> {
                            User user= documentSnapshot1.toObject(User.class);
                            Intent k;
                            if(user.getType().equals("Patient")){
                                k = new Intent(MainActivity.this, HomeActivity.class);
                            }else{
                                k = new Intent(MainActivity.this, DoctorHomeActivity.class);
                                //Snackbar.make(findViewById(R.id.main_layout), "Doctor interface entraint de realisation", Snackbar.LENGTH_SHORT).show();
                            }
                            startActivity(k);
                            finish();
                        });
                    } else {
                        Intent k = new Intent(MainActivity.this, FirstSigninActivity.class);
                        startActivity(k);
                        finish();
                    }
                });
            } catch(Exception e) {
                Log.e(TAG, "Non-Fatal Exception: "+e.getLocalizedMessage());
            }
        }
    }
}
