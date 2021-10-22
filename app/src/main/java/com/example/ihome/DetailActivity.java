package com.example.ihome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ihome.Model.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class DetailActivity extends AppCompatActivity {

    private String id, model, image, name, price, num, addtime, adddate;

    private TextView itemname, itemprice, itemdescr;
    private ImageView itemimage, backIv, shareIv;
    private ImageButton itemsave;
    private Button itemadd, ARModel;

    private ElegantNumberButton quantityBtn;

    private Calendar calendar;
    private SimpleDateFormat currentdate, currenttime;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        id = getIntent().getStringExtra("itemid");

        num = "1";

        itemname = findViewById(R.id.item_title);
        itemprice = findViewById(R.id.item_price);
        itemdescr = findViewById(R.id.item_descr);
        itemimage = findViewById(R.id.item_image);

        calendar = Calendar.getInstance();
        currentdate = new SimpleDateFormat("dd-MMM-yy");
        currenttime = new SimpleDateFormat("HH:mm:ss a");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        readitem();
        saveditem();

        quantityBtn = findViewById(R.id.quantity);

        quantityBtn.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                num = quantityBtn.getNumber();
            }
        });

        ARModel = findViewById(R.id.ardisplay);

        ARModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ARModelActivity.class);
                i.putExtra("itemmodel", model);
                startActivity(i);
            }
        });

        itemsave = findViewById(R.id.item_save);

        itemsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveitem();
            }
        });

        itemadd = findViewById(R.id.item_add);

        itemadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addcart();
            }
        });

        shareIv = findViewById(R.id.share);

        shareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this product from iHome - " + name);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        backIv = findViewById(R.id.back);

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void saveitem() {
        if (itemsave.getTag().equals("save")) {
            FirebaseDatabase.getInstance().getReference().child("Saves")
                    .child(firebaseUser.getUid()).child(id).setValue(true);
        } else {
            FirebaseDatabase.getInstance().getReference().child("Saves")
                    .child(firebaseUser.getUid()).child(id).removeValue();
        }
    }

    public void saveditem() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Save")
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    itemsave.setImageResource(R.drawable.ic_bookmark);
                    itemsave.setTag("saved");
                } else {
                    itemsave.setImageResource(R.drawable.ic_bookmark_border);
                    itemsave.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addcart() {
        adddate = currentdate.format(calendar.getTime());
        addtime = currenttime.format(calendar.getTime());

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart")
                .child(firebaseUser.getUid()).child(id);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", name);
        map.put("price", price);
        map.put("quantity", num);
        map.put("image", image);
        map.put("createdAt", adddate + " " + addtime);

        databaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "Add to cart successfully",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    public void readitem() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Item").child(id);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Item item = dataSnapshot.getValue(Item.class);

                    itemname.setText(item.getTitle());
                    itemdescr.setText(item.getDescr());
                    itemprice.setText("RM" + item.getPrice());

                    model = item.getModel();
                    image = item.getImage();
                    name = item.getTitle();
                    price = item.getPrice();

                    Picasso.get().load(item.getImage()).into(itemimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
