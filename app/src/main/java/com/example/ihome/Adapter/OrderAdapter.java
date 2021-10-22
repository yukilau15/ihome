package com.example.ihome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihome.Model.Order;
import com.example.ihome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context mContext;
    private List<Order> mOrder;

    private FirebaseUser firebaseUser;

    public OrderAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrder = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        final Order order = mOrder.get(position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.orderid.setText(order.getId());
        holder.ordertime.setText(order.getCreatedAt());
        holder.ordertotal.setText("RM" + order.getAmount());
        holder.method.setText(order.getMethod());
        holder.address.setText(order.getAddress());
    }

    @Override
    public int getItemCount() {
        return mOrder.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView orderid, ordertime, ordertotal, method, address;

        public OrderViewHolder(View orderView) {
            super(orderView);

            orderid = orderView.findViewById(R.id.order_id);
            ordertime = orderView.findViewById(R.id.order_time);
            ordertotal = orderView.findViewById(R.id.order_total);
            method = orderView.findViewById(R.id.method);
            address = orderView.findViewById(R.id.address);
        }
    }
}
