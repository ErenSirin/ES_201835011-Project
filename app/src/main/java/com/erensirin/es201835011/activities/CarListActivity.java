package com.erensirin.es201835011.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.erensirin.es201835011.R;
import com.erensirin.es201835011.adapters.CarAdapter;
import com.erensirin.es201835011.databinding.ActivityCarListBinding;
import com.erensirin.es201835011.models.Car;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CarListActivity extends AppCompatActivity {
    private ActivityCarListBinding activityCarListBinding = null;
    private CarAdapter carAdapter = null;
    private ArrayList<Car> carArrayList = new ArrayList<>();

    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser = null;
    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCarListBinding = DataBindingUtil.setContentView(CarListActivity.this, R.layout.activity_car_list);
        activityCarListBinding.setActivityCarListObject(CarListActivity.this);

        firebaseInitialize();
        rvInitialize();
        setItems();

    }

    private void firebaseInitialize() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("CARS");
    }

    private void rvInitialize() {
        activityCarListBinding.recyclerView2.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CarListActivity.this, LinearLayoutManager.VERTICAL, false);
        activityCarListBinding.recyclerView2.setLayoutManager(linearLayoutManager);
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
        carAdapter = new CarAdapter(CarListActivity.this, carArrayList, "CarListActivity");
        carAdapter.notifyDataSetChanged();
        activityCarListBinding.recyclerView2.setAdapter(carAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CarListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}