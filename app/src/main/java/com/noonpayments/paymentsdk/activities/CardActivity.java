package com.noonpayments.paymentsdk.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.noonpayments.paymentsdk.Base.BaseActivity;
import com.noonpayments.paymentsdk.R;
import com.noonpayments.paymentsdk.databinding.ActivityCardBinding;
import com.noonpayments.paymentsdk.helpers.Helper;
import com.noonpayments.paymentsdk.models.Language;
import com.noonpayments.paymentsdk.models.NoonPaymentsCard;

import java.util.ArrayList;

public class CardActivity extends BaseActivity {

    ArrayList<NoonPaymentsCard> savedCards;
    int selectedCard = -1;
    RadioButton rbSelected = null;
    ArrayList<RadioButton> rbCardList = new ArrayList<RadioButton>();
    ArrayList<EditText> edtCVVList = new ArrayList<EditText>();

    Context context;

    private ActivityCardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(getWindow().FEATURE_NO_TITLE);

        binding = ActivityCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFinishOnTouchOutside(false);


        context = setLocale(this, getLanguage());

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //setup the UI
        savedCards = setup.getNoonSavedCards();
        initView();
        initEvents();
        initCardList();
    }

    private void initView() {
        binding.txtAmount.setText(data.getDisplayAmount(userUI.getLanguage()));

        binding.paynowBox.setEnabled(false);

        //set text
        binding.txtBack.setText(context.getResources().getString(R.string.back_payment));
        binding.txtSelectCard.setText(context.getResources().getString(R.string.save_card));
        binding.txtPayNow.setText(context.getResources().getString(R.string.pay_now));
        binding.txtPay.setText(context.getResources().getString(R.string.payable_amount));
        binding.btnNewCard.setText(context.getResources().getString(R.string.add_card));

        //colors & icons
        userUI.setupDialog(this, binding.dialogMode);
        userUI.setupPaynow(this, binding.paymentBox, binding.paynowBox, binding.txtPay, binding.txtAmount, binding.txtPayNow);
        userUI.setupLogo(binding.viewLogo);

        //setup add new button border and colors
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(userUI.getBoxBackgroundColor());
        int rad = Helper.GetPixelsFromDP(this, 8);
        gd.setCornerRadii(new float[]{rad, rad, rad, rad, rad, rad, rad, rad});
        gd.setStroke(2, userUI.getBoxBorderColor());
        binding.btnNewCard.setBackground(gd);
        binding.btnNewCard.setTextColor(userUI.getAddNewCardTextForegroundColor());

        //heading and other text colors
        binding.txtBack.setTextColor(userUI.getPaymentOptionHeadingForeground());
        binding.txtSelectCard.setTextColor(userUI.getBoxForegroundColor());
    }

    private void initEvents() {

        binding.paynowBox.setOnClickListener(view -> doPayment());

        binding.btnNewCard.setOnClickListener(view -> doNewCard());

        binding.btnCancel.setOnClickListener(view -> doCancel());

        binding.viewArrowBack.setOnClickListener(view -> doBack());

        binding.txtBack.setOnClickListener(view -> doBack());
    }

    private void initCardList() {
        userUI.setupBox(this, binding.listBox, 1);

        int index = 0;
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (NoonPaymentsCard card : savedCards) {
            View v = vi.inflate(R.layout.card_layout, null);
            RadioButton rb = (RadioButton) v.findViewById(R.id.rdoCard);
            rb.setTag(index);
            rb.setOnClickListener(view -> {
                if (rbSelected != null) {
                    rbSelected.setChecked(false);
                }
                if (selectedCard != -1) {
                    edtCVVList.get(selectedCard).setText("");
                    edtCVVList.get(selectedCard).setEnabled(false);
                    edtCVVList.get(selectedCard).setBackgroundResource(R.drawable.roundtextbox);
                }
                rbSelected = (RadioButton) view;
                selectedCard = (Integer) rbSelected.getTag();
                edtCVVList.get(selectedCard).setEnabled(true);
                edtCVVList.get(selectedCard).setBackgroundResource(R.drawable.roundtextbox);
            });

            ImageView imgCard = (ImageView) v.findViewById(R.id.viewCard);
            if (card.getCardType().equals("VISA"))
                imgCard.setImageResource(R.drawable.ic_visa);
            else if (card.getCardType().equals("MC"))
                imgCard.setImageResource(R.drawable.ic_mastercard);
            else if (card.getCardType().equals("AMEX"))
                imgCard.setImageResource(R.drawable.ic_americanexpress);
            else if (card.getCardType().equals("MADA"))
                imgCard.setImageResource(R.drawable.ic_mada);
            else
                imgCard.setImageResource(R.drawable.ic_visa);

            TextView textView = (TextView) v.findViewById(R.id.txtCardNumber);
            String cardNumber = card.getCardNumber();
            if (cardNumber.length() > 4)
                cardNumber = cardNumber.substring(cardNumber.length() - 4, cardNumber.length());
            if (userUI.getLanguage() == Language.ENGLISH)
                cardNumber = Helper.getLanguageTextByUser(this, "ending_in", userUI.getLanguage(), "") + " " + cardNumber;
            else
                cardNumber = Helper.getLanguageTextByUser(this, "ending_in", userUI.getLanguage(), "") + " " + cardNumber;
            textView.setText(cardNumber);
            textView.setTextColor(userUI.getBoxForegroundColor());

            EditText edtCVV = (EditText) v.findViewById(R.id.edtRGCVV);
            edtCVV.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    String cvv = s.toString();
                    if (cvv.length() != 3 && cvv.length() != 4) {
                        edtCVV.setBackgroundResource(R.drawable.roundtextboxred);
                    } else {
                        edtCVV.setBackgroundResource(R.drawable.roundtextbox);
                    }

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    enablePaymentButton();
                }
            });

            edtCVVList.add(edtCVV);
            rbCardList.add(rb);

            // insert into main view
            binding.llListContent.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            index++;
        }

    }

    private void doPayment() {
        //validate
        String message = validateEntry();
        if (!message.isEmpty()) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        NoonPaymentsCard card = savedCards.get(selectedCard);

        Intent in = new Intent(this, FinalActivity.class);
        in.putExtra("cvv", edtCVVList.get(selectedCard).getText().toString());
        in.putExtra("cardtoken", card.getCardToken());
        in.putExtra("addnewcard", false);
        in.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(in);
        finish();
    }

    private void enablePaymentButton() {

        String msg = validateEntry();
        if (msg.isEmpty()) {
            binding.paynowBox.setBackgroundColor(userUI.getPaynowBackgroundColorHighlight());
            binding.paynowBox.setEnabled(true);
            binding.txtPayNow.setTextColor(Color.WHITE);
        } else {
            binding.paynowBox.setBackgroundColor(userUI.getPaynowBackgroundColor());
            binding.paynowBox.setEnabled(false);
            binding.txtPayNow.setTextColor(userUI.getPaynowForegroundColor());
        }
    }

    private String validateEntry() {
        String message = "";
        //card has been selected
        if (selectedCard == -1) {
            message = Helper.getLanguageTextByUser(this, "saved_cards", userUI.getLanguage(), "");
            return message;
        }
        //validate CVV
        NoonPaymentsCard card = savedCards.get(selectedCard);
        String cvv = edtCVVList.get(selectedCard).getText().toString();
        if (cvv.isEmpty() || (card.getCardType().equals(Helper.CARD_AMEX) == false && cvv.length() != 3)
                || (card.getCardType().equals(Helper.CARD_AMEX) == true && cvv.length() != 4)) {
            message = Helper.getLanguageTextByUser(this, "cvv_validate", userUI.getLanguage(), "");
            return message;
        }

        return "";
    }

    private void doNewCard() {
        Intent in = new Intent(this, NewCardActivity.class);
        in.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(in);
        finish();
    }

    private void doBack() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("response", "back");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void doCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
        builder.setTitle(context.getResources().getString(R.string.cancel_title));
        builder.setMessage(context.getResources().getString(R.string.cancel_transaction));

        //Yes Button
        String yes = context.getResources().getString(R.string.yes);
        builder.setPositiveButton(yes, (dialog, which) -> {
            callCancelAPI(data.getOrderId());
            Intent resultIntent = new Intent();
            resultIntent.putExtra("response", "cancel");
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        //No Button
        String no = context.getResources().getString(R.string.no);
        builder.setNegativeButton(no, (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (resultCode == Activity.RESULT_OK && requestCode == 2002) {
            String response = data.getStringExtra("response");
            if (response.equals("close")) {
                doCancel();
            } else {
                binding.dialogMode.setVisibility(View.VISIBLE);
            }
        }
    }
}