package com.example.ihome;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.bumptech.glide.Glide;
import com.example.ihome.Model.Card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CardActivity extends AppCompatActivity {

    private TextView cardnameTv, cardnumberTv, validityTv;
    private ImageView closeIv, checkIv, typeIv;

    private CardForm cardForm;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        closeIv = findViewById(R.id.close);
        checkIv = findViewById(R.id.check);
        cardnameTv = findViewById(R.id.tv_card_name);
        cardnumberTv = findViewById(R.id.tv_card_number);
        validityTv = findViewById(R.id.tv_validity);
        typeIv = findViewById(R.id.ivType);

        cardForm = findViewById(R.id.card_form);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        readcard();

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .cardholderName(CardForm.FIELD_REQUIRED)
                .setup(this);

        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        cardForm.setOnCardTypeChangedListener(new CardEditText.OnCardTypeChangedListener() {
            @Override
            public void onCardTypeChanged(CardType cardType) {
                Glide.with(getApplicationContext()).load(cardType.getFrontResource()).into(typeIv);
            }
        });

        cardForm.getCardholderNameEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cardnameTv.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cardForm.getCardEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cardnumberTv.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cardForm.getExpirationDateEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validityTv.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        checkIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardholderName, cardNumber, expirationMonth, expirationYear, cvv;

                cardNumber = cardForm.getCardNumber();
                expirationMonth = cardForm.getExpirationMonth();
                expirationYear = cardForm.getExpirationYear();
                cvv = cardForm.getCvv();
                cardholderName = cardForm.getCardholderName();

                if (!cardForm.isValid()) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the form",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    updateCard(cardholderName, cardNumber, expirationMonth, expirationYear, cvv);
                }
            }
        });

        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void updateCard(final String cardholderName, final String cardNumber,
            final String expirationMonth, final String expirationYear, final String cvv) {
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Card").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("cardholdername", cardholderName);
        map.put("cardnumber", cardNumber);
        map.put("cardmonth", expirationMonth);
        map.put("cardyear", expirationYear);
        map.put("cvv", cvv);

        databaseReference.updateChildren(map);

        Toast.makeText(getApplicationContext(),
                "Update successfully",
                Toast.LENGTH_SHORT)
                .show();

        finish();
    }

    public void readcard() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Card")
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Card card = dataSnapshot.getValue(Card.class);

                    cardForm.getCardholderNameEditText().setText(card.getCardholdername());
                    cardForm.getCardEditText().setText(card.getCardnumber());
                    cardForm.getExpirationDateEditText().setText(card.getCardmonth() + card.getCardyear());
                    cardForm.getCvvEditText().setText(card.getCvv());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
