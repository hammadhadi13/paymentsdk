package com.noonpayments.paymentsdk.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.noonpayments.paymentsdk.Base.BaseActivity;
import com.noonpayments.paymentsdk.R;
import com.noonpayments.paymentsdk.databinding.ActivityModeBinding;
import com.noonpayments.paymentsdk.helpers.Helper;
import com.noonpayments.paymentsdk.models.NoonPaymentsCard;
import com.noonpayments.paymentsdk.models.NoonPaymentsResponse;
import com.noonpayments.paymentsdk.models.NoonPaymentsSetup;

import java.util.ArrayList;

public class ModeActivity extends BaseActivity {
    ArrayList<NoonPaymentsCard> savedCards;
    Context context;
    private ActivityModeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(getWindow().FEATURE_NO_TITLE);
        binding = ActivityModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFinishOnTouchOutside(false);

        // get context to set language...
        if (getIntent().getStringExtra("lang") != null) {
            String lang = getIntent().getStringExtra("lang");
            setLanguage(lang);
        }
        context = setLocale(this, getLanguage());

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //setup the UI
        setup = NoonPaymentsSetup.getInstance();
        setup.getNoonUI().setupColors(this.getApplicationContext());
        userUI = setup.getNoonUI();
        data = setup.getNoonData();
        savedCards = setup.getNoonSavedCards();

        initView();

        initEvents();
    }

    private void initView() {

        //set text
        binding.txtSelect.setText(context.getResources().getString(R.string.payment_method));
        binding.txtCard.setText(context.getResources().getString(R.string.card));
        binding.txtPay.setText(context.getResources().getString(R.string.pay_now));
        binding.txtPaySecure.setText(context.getResources().getString(R.string.your_payments_are_processed_securely));
        binding.txtAmount.setText(data.getDisplayAmount(userUI.getLanguage()));

        String s = data.getDisplayAmount(userUI.getLanguage());

        if (data.getCurrency().equalsIgnoreCase("SAR")) {
            binding.viewmeeza.setVisibility(View.VISIBLE);
            binding.viewmeeza.setImageResource(R.drawable.ic_mada);
        } else if (data.getCurrency().equalsIgnoreCase("EGP")) {
            binding.viewmeeza.setVisibility(View.VISIBLE);
            binding.viewmeeza.setImageResource(R.drawable.meeza);
        }

        //colors & icons
        userUI.setupDialog(this, binding.dialogMode);
        setupCardSection();
        setupPayableSection();
        setTextColor();
        userUI.setupLogo(binding.viewLogo);

    }

    private void initEvents() {
        binding.ccBox.setOnClickListener(view -> gotoCard());

        binding.btnCancel.setOnClickListener(view -> doCancel());
    }

    private void gotoCard() {
        if (savedCards.size() > 0) {
            Intent in = new Intent(this, CardActivity.class);
//            startActivityForResult(in, 2001);
            launchCardActivity.launch(in);
        } else {
            Intent in = new Intent(this, NewCardActivity.class);
//            startActivityForResult(in, 2002);
            launchCardActivity.launch(in);
        }
    }

    private void doCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ModeActivity.this);
        builder.setTitle(context.getResources().getString(R.string.cancel_title));
        builder.setMessage(context.getResources().getString(R.string.cancel_transaction));

        //Yes Button
        String yes = context.getResources().getString(R.string.yes);
        builder.setPositiveButton(yes, (dialog, which) -> {
            callCancelAPI(data.getOrderId());
            NoonPaymentsResponse response = new NoonPaymentsResponse();
            response.setDetails(Helper.STATUS_FAILURE, "Payment cancelled by user", "", "");
            Intent resultIntent = new Intent();
            resultIntent.putExtra("noonresponse", new Gson().toJson(response));
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        //No Button
        String no = context.getResources().getString(R.string.no);
        builder.setNegativeButton(no, (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void doCancelNoAlert() {
        NoonPaymentsResponse response = new NoonPaymentsResponse();
        response.setDetails(Helper.STATUS_FAILURE, "Payment cancelled by user", "", "");
        Intent resultIntent = new Intent();
        resultIntent.putExtra("noonresponse", new Gson().toJson(response));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void doComplete(String response) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("noonresponse", response);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    ActivityResultLauncher<Intent> launchCardActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.hasExtra("response")) {
                            String response = data.getStringExtra("response");
                            if (response.equals("cancel")) {
                                doCancelNoAlert();
                            } else if (response.equals("complete")) {
                                String model = data.getStringExtra("noonresponse");
                                doComplete(model);
                            }
                        }
                    }
                }
            });


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // check if the request code is same as what is passed  here it is 2
//        if (resultCode == Activity.RESULT_OK && (requestCode == 2001 || requestCode == 2002)) {
//            if (data.hasExtra("response")) {
//                String response = data.getStringExtra("response");
//                if (response.equals("cancel")) {
//                    doCancelNoAlert();
//                } else if (response.equals("complete")) {
//                    String r = data.getStringExtra("noonresponse");
//                    doComplete(r);
//                }
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        // prevent "back" from leaving this activity
    }

    private void setupCardSection() {
        int bgColour = userUI.getPaymentOptionBackground();
        int borderColor = userUI.getBoxBorderColor();

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bgColour);
        int rad = Helper.GetPixelsFromDP(this, 8);
        gd.setCornerRadius(rad);
        gd.setStroke(2, borderColor);
        binding.ccBox.setBackground(gd);
        int paddingLR = Helper.GetPixelsFromDP(this, 20);
        int paddingTB = Helper.GetPixelsFromDP(this, 14);
        binding.ccBox.setPadding(paddingTB, paddingLR, paddingTB, paddingTB);
    }

    private void setupPayableSection() {
        int bgColour = userUI.getPaynowBackgroundColorPrimary();
        binding.paymentBox.setBackgroundColor(bgColour);
    }

    private void setTextColor() {
        //text colours
        binding.txtSelect.setTextColor(userUI.getPaymentOptionHeadingForeground());
        binding.txtPaySecure.setTextColor(userUI.getFooterForegroundColor());

        binding.txtCard.setTextColor(userUI.getPaymentOptionForeground());

        binding.txtPay.setTextColor(userUI.getPaynowForegroundColorPrimary());
        binding.txtAmount.setTextColor(userUI.getPaynowForegroundColorPrimary());
    }
}