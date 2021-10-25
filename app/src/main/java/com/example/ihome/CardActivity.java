package com.example.ihome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CardActivity extends AppCompatActivity {

    private String orderid, orderitem, totalprice, method, address, addtime, adddate;

    private TextView totalTv;
    private ImageView closeIv;
    private Button payBtn;

    private CardForm cardForm;

    private Calendar calendar;
    private SimpleDateFormat currentdate, currenttime, currentorder;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        orderitem = getIntent().getStringExtra("order");
        totalprice = getIntent().getStringExtra("subtotal");
        method = getIntent().getStringExtra("method");
        address = getIntent().getStringExtra("address");

        totalTv = findViewById(R.id.payment_amount);
        closeIv = findViewById(R.id.close);
        payBtn = findViewById(R.id.btn_pay);

        cardForm = findViewById(R.id.card_form);

        calendar = Calendar.getInstance();
        currentdate = new SimpleDateFormat("dd-MMM-yy");
        currenttime = new SimpleDateFormat("HH:mm:ss a");
        currentorder = new SimpleDateFormat("yyyyMMddHHmmss");

        totalTv.setText("RM" + totalprice);
        payBtn.setText("Payment");

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(com.craftman.cardform.Card card) {
                confirmorder();
                updateaddress();
            }
        });

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void confirmorder() {
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

    private void updateaddress() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("User").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("address", address);

        reference.updateChildren(map);
    }
}
