package com.erensirin.es201835011.adapters;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.erensirin.es201835011.R;
import com.erensirin.es201835011.activities.MainActivity;
import com.erensirin.es201835011.models.Car;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CardViewDesignThigsHolder>{
    private Context context = null;
    private ArrayList<Car> carArrayList = new ArrayList<>();
    private String fromActivity = null;

    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;

    public CarAdapter(Context context, ArrayList<Car> carArrayList, String fromActivity) {
        this.context = context;
        this.carArrayList = carArrayList;
        this.fromActivity = fromActivity;
    }

    @NonNull
    @Override
    public CardViewDesignThigsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardcar, parent, false);
        return new CardViewDesignThigsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewDesignThigsHolder holder, int position) {
        Car car = carArrayList.get(position);

        Log.e("Car photo", car.getPhoto());

        if (!car.getPhoto().isEmpty()) {
            Picasso.get().load(Uri.parse(car.getPhoto())).into(holder.imageView6);
        }else {
            Picasso.get().load(Uri.parse("https://firebasestorage.googleapis.com/v0/b/es-201835011.appspot.com/o/CARS%2Fcar.png?alt=media&token=27c11211-e2d3-4715-90e2-91dce0cb1d8a")).into(holder.imageView6);
        }

        holder.textView15.setText(car.getCar());
        holder.textView17.setText(car.getCardetail());
        holder.textView22.setText(car.getPrice());

        holder.imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromActivity.equals("CarListActivity")) {
                    updateCar(car);
                }else {
                    showAlertDialog(context.getString(R.string.notupdatephoto));
                }
            }
        });

        holder.constraintLayout80.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAlertDialog("Car: "+car.getCar()+"\nCar Detail: "+car.getCardetail()+"\nCar Price: "+car.getPrice());
                return true;
            }
        });

        if (fromActivity.equals("CarListActivity")) {
            holder.textView27.setVisibility(View.INVISIBLE);
            holder.textView28.setVisibility(View.INVISIBLE);
            holder.textView29.setVisibility(View.INVISIBLE);
            holder.textView30.setVisibility(View.INVISIBLE);
            holder.textView23.setText(context.getString(R.string.cardtext));
            holder.cardView8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDateDialog(car);
                }
            });
        }else {
            holder.textView27.setVisibility(View.VISIBLE);
            holder.textView28.setVisibility(View.VISIBLE);
            holder.textView29.setVisibility(View.VISIBLE);
            holder.textView30.setVisibility(View.VISIBLE);
            holder.textView28.setText(car.getStartdate());
            holder.textView30.setText(car.getEnddate());
            holder.textView23.setText(context.getString(R.string.cardtext2));

            holder.cardView8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initializeFirebase();
                    databaseReference = firebaseDatabase.getReference("CARS");

                    HashMap<String, String> carMap = new HashMap<>();

                    carMap.put("car", car.getCar());
                    carMap.put("cardetail", car.getCardetail());
                    carMap.put("photo", car.getPhoto());
                    carMap.put("price", car.getPrice());

                    databaseReference.push().setValue(carMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            initializeFirebase();
                            databaseReference = firebaseDatabase.getReference("RENTEDCARS");

                            databaseReference.child(car.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    showAlertDialog(context.getString(R.string.alertmessage7));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showAlertDialog(context.getString(R.string.alertmessage8));
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showAlertDialog(e.getMessage());
                        }
                    });
                }
            });

        }

    }

    private void updateCar(Car carobj) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view2 = LayoutInflater.from(context).inflate(R.layout.addcardialog, null, false);

        EditText editText6 = view2.findViewById(R.id.editText6);
        EditText editText7 = view2.findViewById(R.id.editText7);
        EditText editText8 = view2.findViewById(R.id.editText8);

        CardView cardView4 = view2.findViewById(R.id.cardView4);
        CardView cardView5 = view2.findViewById(R.id.cardView5);

        bottomSheetDialog.setContentView(view2);

        editText6.setText(carobj.getCar());
        editText7.setText(carobj.getCardetail());
        editText8.setText(carobj.getPrice());

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeFirebase();
                databaseReference = firebaseDatabase.getReference("CARS");

                String car = editText6.getText().toString().trim();
                String cardetail = editText7.getText().toString().trim();
                String price = editText8.getText().toString().trim();

                HashMap<String, String> carMap = new HashMap<>();
                carMap.put("car", car);
                carMap.put("cardetail", cardetail);
                carMap.put("photo", carobj.getPhoto());
                carMap.put("price", price);

                databaseReference.push().setValue(carMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        databaseReference.child(carobj.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                bottomSheetDialog.dismiss();
                                showAlertDialog(context.getString(R.string.updated));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showAlertDialog(e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showAlertDialog(e.getMessage());
                    }
                });

            }
        });

        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(context.getString(R.string.notupdatephoto2));
            }
        });

        bottomSheetDialog.create();
        bottomSheetDialog.show();

    }

    @Override
    public int getItemCount() {
        return carArrayList.size();
    }


    public static class CardViewDesignThigsHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout constraintLayout80 = null;
        private TextView textView15, textView17, textView22 = null;
        private TextView textView23, textView27, textView28, textView29, textView30 = null;
        private ImageView imageView6, imageView8 = null;
        private CardView cardView8 = null;

        public CardViewDesignThigsHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout80 = itemView.findViewById(R.id.constraintLayout80);
            textView15 = itemView.findViewById(R.id.textView15);
            textView17 = itemView.findViewById(R.id.textView17);
            textView22 = itemView.findViewById(R.id.textView22);
            textView23 = itemView.findViewById(R.id.textView23);
            textView27 = itemView.findViewById(R.id.textView27);
            textView28 = itemView.findViewById(R.id.textView28);
            textView29 = itemView.findViewById(R.id.textView29);
            textView30 = itemView.findViewById(R.id.textView30);
            imageView6 = itemView.findViewById(R.id.imageView6);
            imageView8 = itemView.findViewById(R.id.imageView8);
            cardView8 = itemView.findViewById(R.id.cardView8);
        }
    }

    private void initializeFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void showDateDialog(Car car) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.rentacardialog, null, false);
        builder.setView(view);

        EditText editText15 = view.findViewById(R.id.editText15);
        EditText editText16 = view.findViewById(R.id.editText16);

        editText15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        if (i1 == 0 || i1 == 1 || i1 == 2 || i1 == 3 || i1 == 4 || i1 == 5 || i1 == 6 || i1 == 7 || i1 == 8 || i1 == 9) {
                            editText15.setText(i2+"/0"+(i1+1)+"/"+i);
                        }else {
                            editText15.setText(i2+"/"+(i1+1)+"/"+i);
                        }

                    }
                }, year, month, day);

                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", datePickerDialog);

                datePickerDialog.create();
                datePickerDialog.show();
            }
        });

        editText16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        if (i1 == 0 || i1 == 1 || i1 == 2 || i1 == 3 || i1 == 4 || i1 == 5 || i1 == 6 || i1 == 7 || i1 == 8 || i1 == 9) {
                            editText16.setText(i2+"/0"+(i1+1)+"/"+i);
                        }else {
                            editText16.setText(i2+"/"+(i1+1)+"/"+i);
                        }
                    }
                }, year, month, day);

                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", datePickerDialog);

                datePickerDialog.create();
                datePickerDialog.show();

            }
        });

        builder.setPositiveButton("OLUŞTUR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String startdate = editText15.getText().toString().trim();
                String enddate = editText16.getText().toString().trim();

                initializeFirebase();
                databaseReference = firebaseDatabase.getReference("RENTEDCARS");

                HashMap<String, String> rentalCarMap = new HashMap<>();

                rentalCarMap.put("car", car.getCar());
                rentalCarMap.put("cardetail", car.getCardetail());
                rentalCarMap.put("price", car.getPrice());
                rentalCarMap.put("startdate", startdate);
                rentalCarMap.put("enddate", enddate);
                rentalCarMap.put("photo", car.getPhoto());

                databaseReference.push().setValue(rentalCarMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        databaseReference = firebaseDatabase.getReference("CARS");
                        databaseReference.child(car.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showAlertDialog(context.getString(R.string.alertmessage5));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showAlertDialog(context.getString(R.string.alertmessage6));
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showAlertDialog(context.getString(R.string.alertmessage6));
                    }
                });
            }
        });
        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    private void showAlertDialog(String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setMessage(message);
            builder.setPositiveButton(context.getString(R.string.alertbutton), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }catch (Exception e) {
            Log.e("Alert Error", e.getMessage());
        }
    }
}
