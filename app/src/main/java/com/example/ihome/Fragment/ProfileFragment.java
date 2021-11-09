package com.example.ihome.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ihome.Model.User;
import com.example.ihome.ProfileActivity;
import com.example.ihome.PurchaseActivity;
import com.example.ihome.R;
import com.example.ihome.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<>();

    private TextView usernameTv, emailTv;
    private Button signoutBtn;

    private de.hdodenhof.circleimageview.CircleImageView profileIv;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        listView = view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        arrayList.add("My Purchases");
        arrayList.add("My Profile");

        usernameTv = view.findViewById(R.id.username);
        emailTv = view.findViewById(R.id.email);
        profileIv = view.findViewById(R.id.profile);
        signoutBtn = view.findViewById(R.id.signout);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        readprofile();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    Intent i = new Intent(getContext(), PurchaseActivity.class);
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(getContext(), ProfileActivity.class);
                    startActivity(i);
                }
            }
        });

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });

        return view;
    }

    public void readprofile() {
        databaseReference = FirebaseDatabase.getInstance().getReference("User")
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                usernameTv.setText(user.getUsername());
                emailTv.setText(user.getEmail());

                if (getActivity() == null) {
                    return;
                }

                Glide.with(getActivity()).load(user.getImage()).into(profileIv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void signout() {
        mAuth.signOut();

        Intent i = new Intent(getActivity(), StartActivity.class);
        startActivity(i);
        getActivity().finish();

        Toast.makeText(getContext(),
                "Sign out successfully",
                Toast.LENGTH_SHORT)
                .show();
    }
}
