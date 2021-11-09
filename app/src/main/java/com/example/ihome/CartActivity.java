package com.example.ihome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihome.Adapter.CartAdapter;
import com.example.ihome.Model.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private String orderItem;
    private int productprice, totalprice;

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Cart> cartList = new ArrayList<>();

    private TextView totalTv;
    private ImageView backIv;
    private Button checkoutBtn, mainBtn;

    private LinearLayout emptyLayout, totalLayout;

    private ArrayList<String> orderItemList = new ArrayList<>();

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        totalTv = findViewById(R.id.total);
        backIv = findViewById(R.id.back);
        checkoutBtn = findViewById(R.id.checkout);
        mainBtn = findViewById(R.id.main);

        emptyLayout = findViewById(R.id.emptysec);
        totalLayout = findViewById(R.id.totalsec);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        emptyLayout.setVisibility(View.VISIBLE);
        totalLayout.setVisibility(View.INVISIBLE);

        readitem();

        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderItem = TextUtils.join(", ", orderItemList);

                Intent i = new Intent(getApplicationContext(), CheckoutActivity.class);
                i.putExtra("subtotal", "" + totalprice);
                i.putExtra("order", orderItem);
                startActivity(i);
            }
        });

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void readitem() {
        Query reference = FirebaseDatabase.getInstance().getReference("Cart")
                .child(firebaseUser.getUid()).orderByChild("createdAt");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartList.clear();
                totalprice = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cart cart = snapshot.getValue(Cart.class);
                    cartList.add(cart);

                    orderItemList.add(cart.getTitle() + " (" + cart.getQuantity() + ")");
                    productprice = (Integer.valueOf(cart.getPrice())) * (Integer.valueOf(cart.getQuantity()));
                    totalprice += productprice;
                    totalTv.setText("RM" + totalprice);
                }

                if (cartList.isEmpty()) {
                    emptyLayout.setVisibility(View.VISIBLE);
                    totalLayout.setVisibility(View.INVISIBLE);
                } else {
                    emptyLayout.setVisibility(View.INVISIBLE);
                    totalLayout.setVisibility(View.VISIBLE);
                }

                cartAdapter = new CartAdapter(getApplicationContext(), cartList);
                recyclerView.setAdapter(cartAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
