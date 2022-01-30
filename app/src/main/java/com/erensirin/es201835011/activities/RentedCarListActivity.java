package com.erensirin.es201835011.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.erensirin.es201835011.R;
import com.erensirin.es201835011.adapters.CarAdapter;
import com.erensirin.es201835011.databinding.ActivityRentedCarListBinding;
import com.erensirin.es201835011.models.Car;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RentedCarListActivity extends AppCompatActivity {
    private ActivityRentedCarListBinding activityRentedCarListBinding = null;
    private CarAdapter carAdapter = null;
    private ArrayList<Car> carArrayList = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRentedCarListBinding = DataBindingUtil.setContentView(RentedCarListActivity.this, R.layout.activity_rented_car_list);
        activityRentedCarListBinding.setActivityRentalCarListObject(RentedCarListActivity.this);

        firebaseInitialize();
        rvInitialize();
        setItems();

    }

    private void firebaseInitialize() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("RENTEDCARS");
    }

    private void rvInitialize() {
        activityRentedCarListBinding.recyclerView3.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RentedCarListActivity.this, LinearLayoutManager.VERTICAL, false);
        activityRentedCarListBinding.recyclerView3.setLayoutManager(linearLayoutManager);
    }

    private void setItems() {
        carArrayList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carArrayList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    Car car = new Car(d.getKey(), String.valueOf(d.child("car").getValue()), String.valueOf(d.child("cardetail").getValue()), String.valueOf(d.child("price").getValue()), String.valueOf(d.child("startdate").getValue()), String.valueOf(d.child("enddate").getValue()), String.valueOf(d.child("photo").getValue()));
                    carArrayList.add(car);
                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapter() {
        carAdapter = new CarAdapter(RentedCarListActivity.this, carArrayList, "RentedCarListActivity");
        carAdapter.notifyDataSetChanged();
        activityRentedCarListBinding.recyclerView3.setAdapter(carAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RentedCarListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}