package com.example.ihome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihome.Model.Card;
import com.example.ihome.Model.Item;
import com.example.ihome.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CheckoutActivity extends AppCompatActivity {

    private String orderid, orderitem, addtime, adddate;
    private String totalprice;

    private TextInputEditText addressEt;
    private TextView totalTv;
    private ImageView backIv;
    private Button payBtn;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private Calendar calendar;
    private SimpleDateFormat currentdate, currenttime, currentorder;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        totalprice = getIntent().getStringExtra("subtotal");
        orderitem = getIntent().getStringExtra("order");

        addressEt = findViewById(R.id.address);
        totalTv = findViewById(R.id.subtotal);
        backIv = findViewById(R.id.back);
        payBtn = findViewById(R.id.payment);
        radioGroup = findViewById(R.id.radioGroup);

        calendar = Calendar.getInstance();
        currentdate = new SimpleDateFormat("dd-MMM-yy");
        currenttime = new SimpleDateFormat("HH:mm:ss a");
        currentorder = new SimpleDateFormat("yyyyMMddHHmmss");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        totalTv.setText("RM" + totalprice);

        readaddress();

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address;
                address = addressEt.getText().toString();

                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter address",
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getApplicationContext(),
                            "Please select a payment method",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    int radioid = radioGroup.getCheckedRadioButtonId();
                    radioButton = findViewById(radioid);

                    String method = radioButton.getText().toString();

                    if (method.equals("Credit / Debit Card")) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Card")
                                .child(firebaseUser.getUid());

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.exists()) {
                                    Toast.makeText(getApplicationContext(),
                                            "Please add a debit or credit card\nto complete your purchase",
                                            Toast.LENGTH_SHORT)
                                            .show();

                                    Intent i = new Intent(getApplicationContext(), CardActivity.class);
                                    startActivity(i);
                                } else {
                                    confirmorder(method, address);
                                    updateaddress(address);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        confirmorder(method, address);
                        updateaddress(address);
                    }
                }
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void confirmorder(String method, String address) {
        adddate = currentdate.format(calendar.getTime());
        addtime = currenttime.format(calendar.getTime());
        orderid = currentorder.format(calendar.getTime());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Order").child(firebaseUser.getUid()).child(orderid);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", orderid);
        map.put("item", orderitem);
        map.put("amount", totalprice);
        map.put("method", method);
        map.put("address", address);
        map.put("createdAt", adddate + " " + addtime);

        reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "Purchase successfully",
                            Toast.LENGTH_SHORT)
                            .show();

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("Cart")
                .child(firebaseUser.getUid()).removeValue();
    }

    private void updateaddress(String address) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("User").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("address", address);

        reference.updateChildren(map);
    }

    private void readaddress() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                addressEt.setText(user.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
