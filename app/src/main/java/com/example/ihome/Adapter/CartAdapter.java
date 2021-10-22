package com.example.ihome.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihome.DetailActivity;
import com.example.ihome.Model.Cart;
import com.example.ihome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context mContext;
    private List<Cart> mCart;

    private FirebaseUser firebaseUser;

    public CartAdapter(Context context, List<Cart> cartList) {
        mContext = context;
        mCart = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        final Cart cart = mCart.get(position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.itemname.setText(cart.getTitle());
        holder.itemprice.setText("Price: RM" + cart.getPrice());
        holder.itemquantity.setText("Quantity: " + cart.getQuantity());

        Picasso.get().load(cart.getImage()).into(holder.itemimage);

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit:
                                Intent i = new Intent(mContext, DetailActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("itemid", cart.getId());
                                mContext.startActivity(i);

                                return true;
                            case R.id.delete:
                                FirebaseDatabase.getInstance().getReference("Cart")
                                        .child(firebaseUser.getUid()).child(cart.getId()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(mContext,
                                                            cart.getTitle() + " deleted",
                                                            Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                            }
                                        });

                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.inflate(R.menu.cart_menu);
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCart.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        private ImageView itemimage, more;
        private TextView itemname, itemprice, itemquantity;

        public CartViewHolder(@NonNull View cartView) {
            super(cartView);

            itemimage = cartView.findViewById(R.id.item_image);
            itemname = cartView.findViewById(R.id.item_title);
            itemprice = cartView.findViewById(R.id.item_price);
            itemquantity = cartView.findViewById(R.id.item_quantity);
            more = cartView.findViewById(R.id.more_btn);
        }
    }
}
