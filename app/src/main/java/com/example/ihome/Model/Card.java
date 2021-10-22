package com.example.ihome.Model;

public class Card {
    private String cardholdername, cardnumber, cardmonth, cardyear, cvv;

    public Card() {
    }

    public Card(String cardholdername, String cardnumber, String cardmonth, String cardyear, String cvv) {
        this.cardholdername = cardholdername;
        this.cardnumber = cardnumber;
        this.cardmonth = cardmonth;
        this.cardyear = cardyear;
        this.cvv = cvv;
    }

    public String getCardholdername() {
        return cardholdername;
    }

    public void setCardholdername(String cardholdername) {
        this.cardholdername = cardholdername;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public String getCardmonth() {
        return cardmonth;
    }

    public void setCardmonth(String cardmonth) {
        this.cardmonth = cardmonth;
    }

    public String getCardyear() {
        return cardyear;
    }

    public void setCardyear(String cardyear) {
        this.cardyear = cardyear;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
