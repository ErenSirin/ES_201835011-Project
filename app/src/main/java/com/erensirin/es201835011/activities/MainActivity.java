package com.erensirin.es201835011.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.erensirin.es201835011.R;
import com.erensirin.es201835011.databinding.ActivityMainBinding;
import com.erensirin.es201835011.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding = null;

    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser = null;
    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;
    private FirebaseStorage firebaseStorage = null;
    private StorageReference storageReference = null;

    private ProgressDialog progressDialog = null;

    private User user = null;
    private Uri photouri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        activityMainBinding.setMainActivityObject(MainActivity.this);

        initializeFirebase();
        //setOnClickListeners();

    }

    public void showAppInformation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.information));
        builder.setPositiveButton(getString(R.string.alertbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void showSigninDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.signindialog, null, false);

        EditText editText = view.findViewById(R.id.editText);
        EditText editText2 = view.findViewById(R.id.editText2);
        CardView cardView = view.findViewById(R.id.cardView);
        TextView textView3 = view.findViewById(R.id.textView3);
        TextView textView5 = view.findViewById(R.id.textView5);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.create();
        bottomSheetDialog.show();

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editText.getText().toString().trim();
                String password = editText2.getText().toString();

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 3) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            bottomSheetDialog.dismiss();
                            firebaseUser = firebaseAuth.getCurrentUser();
                            activityMainBinding.setMembershipcontroller(true);
                            showAlertDialog(getString(R.string.alertmessage));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            bottomSheetDialog.dismiss();
                            showAlertDialog(getString(R.string.alertmessage2));
                        }
                    });
                }else {
                    Snackbar.make(view, getString(R.string.membershipcontroller), Snackbar.LENGTH_LONG).show();
                }

            }
        });

        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                showSignupDialog();
            }
        });

    }

    public void showSignupDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.signupdialog, null, false);

        EditText editText3 = view.findViewById(R.id.editText3);
        EditText editText4 = view.findViewById(R.id.editText4);
        EditText editText5 = view.findViewById(R.id.editText5);
        CardView cardView2 = view.findViewById(R.id.cardView2);
        TextView textView4 = view.findViewById(R.id.textView4);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.create();
        bottomSheetDialog.show();

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = firebaseDatabase.getReference("USERS");

                String fullname = editText3.getText().toString().trim();
                String email = editText4.getText().toString().trim();
                String password = editText5.getText().toString();

                if (fullname.length() != 0 && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 3) {

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            HashMap<String, String> userMap = new HashMap<>();

                            userMap.put("uid", authResult.getUser().getUid());
                            userMap.put("fullname", fullname);
                            userMap.put("email", email);
                            userMap.put("password", password);

                            databaseReference.push().setValue(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    bottomSheetDialog.dismiss();
                                    firebaseUser = firebaseAuth.getCurrentUser();
                                    activityMainBinding.setMembershipcontroller(true);
                                    showAlertDialog(getString(R.string.alertmessage3));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    bottomSheetDialog.dismiss();
                                    showAlertDialog(getString(R.string.alertmessage4));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            bottomSheetDialog.dismiss();
                            showAlertDialog(getString(R.string.alertmessage4));
                        }
                    });

                }else {
                    Snackbar.make(view, getString(R.string.membershipcontroller), Snackbar.LENGTH_LONG).show();
                }

            }
        });

        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                showSigninDialog();

            }
        });

    }

    public void findUserController() {
        databaseReference = firebaseDatabase.getReference("USERS");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    user = d.getValue(User.class);

                    assert user != null;
                    if (user.getUid().equals(firebaseAuth.getCurrentUser().getUid())) {
                        showLogOutDialog(user);
                    }else {
                        showAlertDialog(getString(R.string.alertmessage9));
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showLogOutDialog(User user) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.logoutdialog, null, false);
        bottomSheetDialog.setContentView(view);

        TextView textView12 = view.findViewById(R.id.textView12);
        TextView textView25 = view.findViewById(R.id.textView25);
        CardView cardView8 = view.findViewById(R.id.cardView8);

        textView12.setText(user.getFullname());
        textView25.setText(user.getEmail());

        cardView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                firebaseAuth.signOut();
                activityMainBinding.setMembershipcontroller(false);
                showAlertDialog(getString(R.string.alertmessage10));
            }
        });

        bottomSheetDialog.create();
        bottomSheetDialog.show();
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            activityMainBinding.setMembershipcontroller(true);
        }else {
            activityMainBinding.setMembershipcontroller(false);
        }

    }

    public void addCar() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View view2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.addcardialog, null, false);

        EditText editText6 = view2.findViewById(R.id.editText6);
        EditText editText7 = view2.findViewById(R.id.editText7);
        EditText editText8 = view2.findViewById(R.id.editText8);

        CardView cardView4 = view2.findViewById(R.id.cardView4);
        CardView cardView5 = view2.findViewById(R.id.cardView5);

        bottomSheetDialog.setContentView(view2);

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = firebaseDatabase.getReference("CARS");

                String car = editText6.getText().toString().trim();
                String cardetail = editText7.getText().toString().trim();
                String price = editText8.getText().toString().trim();

                if (!car.isEmpty() && !cardetail.isEmpty() && !price.isEmpty()) {
                    HashMap<String, String> carMap = new HashMap<>();

                    carMap.put("car", car);
                    carMap.put("cardetail", cardetail);
                    carMap.put("price", price + " ₺");

                    if (photouri != null) {
                        carMap.put("photo", String.valueOf(photouri));
                    }else {
                        carMap.put("photo", "");
                    }

                    databaseReference.push().setValue(carMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            bottomSheetDialog.dismiss();
                            showAlertDialog(getString(R.string.addcarsuccess));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showAlertDialog(getString(R.string.addcarfailure));
                        }
                    });

                }else {
                    showAlertDialog(getString(R.string.addcarcontroller));
                }

            }
        });

        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        bottomSheetDialog.create();
        bottomSheetDialog.show();
    }

    private void choosePhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void carlist() {
        Intent intent = new Intent(MainActivity.this, CarListActivity.class);
        startActivity(intent);
        finish();
    }

    public void rentalcarlist() {
        Intent intent = new Intent(MainActivity.this, RentedCarListActivity.class);
        startActivity(intent);
        finish();
    }

    public void showMembershipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.alertmessage11));
        builder.setPositiveButton(getString(R.string.alertbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.alertbutton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            progressDialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.progresstext), false);
            progressDialog.setCancelable(false);

            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference("CARS").child(String.valueOf(Calendar.getInstance().getTimeInMillis()));

            storageReference.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            photouri = uri;
                            progressDialog.dismiss();
                            showAlertDialog(getString(R.string.alertmessage12));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            showAlertDialog(e.getMessage());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    showAlertDialog(e.getMessage());
                }
            });

        }

    }
}