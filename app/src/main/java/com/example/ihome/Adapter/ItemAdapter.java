package com.example.ihome.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihome.DetailActivity;
import com.example.ihome.Model.Item;
import com.example.ihome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context mContext;
    private List<Item> mItem;

    private FirebaseUser firebaseUser;

    public ItemAdapter(Context context, List<Item> itemList) {
        mContext = context;
        mItem = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        final Item item = mItem.get(position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.itemname.setText(item.getTitle());
        holder.itemprice.setText("RM" + item.getPrice());
        Picasso.get().load(item.getImage()).into(holder.itemimage);

        isSaved(item.getId(), holder.itemsave);

        holder.itemsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.itemsave.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Save")
                            .child(firebaseUser.getUid()).child(item.getId()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Save")
                            .child(firebaseUser.getUid()).child(item.getId()).removeValue();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, DetailActivity.class);
                i.putExtra("itemid", item.getId());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public void isSaved(final String itemid, final ImageView imageView) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Save")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(itemid).exists()) {
                    imageView.setImageResource(R.drawable.ic_bookmark);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_bookmark_border);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemimage, itemsave;
        public TextView itemname, itemprice;

        public ItemViewHolder(View itemView) {
            super(itemView);

            itemimage = itemView.findViewById(R.id.item_image);
            itemname = itemView.findViewById(R.id.item_title);
            itemprice = itemView.findViewById(R.id.item_price);
            itemsave = itemView.findViewById(R.id.item_save);
        }
    }
}
