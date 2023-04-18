package com.noonpayments.paymentsdk.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.noonpayments.paymentsdk.R;
import com.noonpayments.paymentsdk.helpers.Helper;
import com.noonpayments.paymentsdk.models.NoonPaymentsData;
import com.noonpayments.paymentsdk.models.NoonPaymentsSetup;
import com.noonpayments.paymentsdk.models.NoonPaymentsUI;

import java.util.Arrays;
import java.util.Calendar;

public class NewCardActivity extends BaseActivity {

    NoonPaymentsSetup setup;
    NoonPaymentsUI userUI;
    NoonPaymentsData data;
    RelativeLayout dialogMode, ccBox, paymentBox, paynowBox;
    TextView txtBack, txtCardNumber, txtCardName, txtExp, txtCVV, txtPay, txtAmount, txtPayNow, txtAddNewCard,
            txtCardError, txtExpiryError, txtCVVError, txtCardNameError;
    EditText edtCardNumber, edtCardName, edtExp, edtCVV;
    ImageView viewBack, viewCard, viewLogo;
    CheckBox checkBox;
    ImageButton btnCancel;
    int validationStatus = 0;
    String validationMessage = "";
    Context context;
    Boolean isSaveCard = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_card);
        setFinishOnTouchOutside(false);

        // get context to set language...
        context = setLocale(this, language);

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //setup the UI
        setup = NoonPaymentsSetup.getInstance();
        userUI = setup.getNoonUI();
        data = setup.getNoonData();

        initView();

        initEvents();

    }

    private void initView() {

        dialogMode = (RelativeLayout) findViewById(R.id.dialogMode);
        ccBox = findViewById(R.id.cardEntryBox);
        paymentBox = (RelativeLayout) findViewById(R.id.paymentBox);
        paynowBox = (RelativeLayout) findViewById(R.id.paynowBox);
        viewCard = (ImageView) findViewById(R.id.viewCard);
        viewLogo = (ImageView) findViewById(R.id.viewLogo);

        btnCancel = (ImageButton) findViewById((R.id.btnCancel));

        viewBack = (ImageView) findViewById(R.id.viewArrowBack);
        txtBack = (TextView) findViewById(R.id.txtBack);
        txtCardName = (TextView) findViewById(R.id.txtCardName);
        txtCardNumber = (TextView) findViewById(R.id.txtCardNumber);
        txtCVV = (TextView) findViewById(R.id.txtCVV);
        txtExp = (TextView) findViewById(R.id.txtExpiry);
        txtPay = (TextView) findViewById(R.id.txtPay);
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        txtPayNow = (TextView) findViewById(R.id.txtPayNow);
        txtAddNewCard = (TextView) findViewById(R.id.txtAddNewCard);

        txtCardError = findViewById(R.id.txtCardError);
        txtExpiryError = findViewById(R.id.txtExpiryError);
        txtCVVError = findViewById(R.id.txtCVVError);
        txtCardNameError = findViewById(R.id.txtCardNameError);


        edtCardName = (EditText) findViewById(R.id.edtCardName);
        edtCardNumber = (EditText) findViewById(R.id.edtCardNumber);
        edtExp = (EditText) findViewById(R.id.edtExpiry);
        edtCVV = (EditText) findViewById(R.id.edtCVV);

        checkBox = findViewById(R.id.saveCardcb);

        setText();

        paynowBox.setEnabled(false);

        //set text
        if (setup.getNoonSavedCards().size() > 0)
            txtBack.setText(context.getResources().getString(R.string.back_saved));
        else
            txtBack.setText(context.getResources().getString(R.string.back_payment));

        txtCardName.setText(context.getResources().getString(R.string.name_on_card));
        txtCardNumber.setText(context.getResources().getString(R.string.card_number));
        txtCVV.setText(context.getResources().getString(R.string.cvv));
        txtExp.setText(context.getResources().getString(R.string.expiry));

        txtPayNow.setText(context.getResources().getString(R.string.pay_now));
        txtPay.setText(context.getResources().getString(R.string.payable_amount));

        txtAddNewCard.setText(context.getResources().getString(R.string.add_card));
        txtAmount.setText(data.getDisplayAmount(userUI.getLanguage()));

        //set hints
        edtCardName.setHint(context.getResources().getString(R.string.name_validate));
        edtCardNumber.setHint(context.getResources().getString(R.string.number_validate));
        edtExp.setHint(context.getResources().getString(R.string.mmyy));
        edtCVV.setHint(context.getResources().getString(R.string.secure_code));

        //colors & icons
        userUI.setupDialog(this, dialogMode);
        userUI.setupPaynow(this, paymentBox, paynowBox, txtPay, txtAmount, txtPayNow);
        userUI.setupBox(this, ccBox, 2);
        userUI.setupLogo(viewLogo);

        //text colours
        setTextColours();

    }

    private void setText() {

    }

    private void initEvents() {
        paynowBox.setOnClickListener(view -> doPayment());

        txtBack.setOnClickListener(view -> doBack());

        viewBack.setOnClickListener(view -> doBack());

        btnCancel.setOnClickListener(view -> doCancel());


        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isSaveCard = isChecked;

        });

        edtCardNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String errorMsg = checkCardNumberValidation(edtCardNumber.getText().toString());
                txtCardError.setText(errorMsg);
                txtCardError.setVisibility(View.VISIBLE);
                edtCardNumber.setBackgroundResource(R.drawable.roundtextboxred);
                if (errorMsg.isEmpty()) {
                    txtCardError.setVisibility(View.GONE);
                    edtCardNumber.setBackgroundResource(R.drawable.roundtextbox);

                }
                if (!edtCVV.getText().toString().isEmpty()) {
                    errorMsg = cvvValidation(edtCVV.getText().toString(), edtCardNumber.getText().toString());
                    if (errorMsg.isEmpty()) {
                        edtCVV.setBackgroundResource(R.drawable.roundtextbox);
                        txtCVVError.setVisibility(View.GONE);
                    } else {
                        txtCVVError.setText(errorMsg);
                        txtCVVError.setVisibility(View.VISIBLE);
                        edtCVV.setBackgroundResource(R.drawable.roundtextboxred);
                    }
                }
            }
        });

        edtExp.setOnFocusChangeListener(((v, hasFocus) -> {
            if (!hasFocus) {
                String errorMsg = checkExpiryDateValidation(edtExp.getText().toString());
                txtExpiryError.setText(errorMsg);
                txtExpiryError.setVisibility(View.VISIBLE);
                edtExp.setBackgroundResource(R.drawable.roundtextboxred);
                if (errorMsg.isEmpty()) {
                    txtExpiryError.setVisibility(View.GONE);
                    edtExp.setBackgroundResource(R.drawable.roundtextbox);
                }
            }
        }));

        edtCVV.setOnFocusChangeListener(((v, hasFocus) -> {
            if (!hasFocus) {
                String errorMsg = cvvValidation(edtCVV.getText().toString(), edtCardNumber.getText().toString());
                txtCVVError.setText(errorMsg);
                txtCVVError.setVisibility(View.VISIBLE);
                edtCVV.setBackgroundResource(R.drawable.roundtextboxred);
                if (errorMsg.isEmpty()) {
                    edtCVV.setBackgroundResource(R.drawable.roundtextbox);
                    txtCVVError.setVisibility(View.GONE);
                }
            }
        }));

        edtCardName.setOnFocusChangeListener(((v, hasFocus) -> {
            if (!hasFocus) {
                if (edtCardName.getText().toString().isEmpty()) {
                    txtCardNameError.setText(context.getResources().getString(R.string.enter_name_on_card));
                    txtCardNameError.setVisibility(View.VISIBLE);
                    edtCardName.setBackgroundResource(R.drawable.roundtextboxred);
                } else {
                    txtCardNameError.setVisibility(View.GONE);
                    edtCardName.setBackgroundResource(R.drawable.roundtextbox);

                }
            }
        }));

        edtCardNumber.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 24; // size of pattern 0000-0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 20; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = ' ';

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
                String cardNumber = s.toString();
                validateEntry(edtCardName.getText().toString(), cardNumber, edtExp.getText().toString(), edtCVV.getText().toString());
                enablePaymentButton();

                if (s.length() == 0) {
                    edtCVV.setText("");
                    edtExp.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                displayCardType(s.toString());
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // check that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private void displayCardType(String cn) {
                String cardType = Helper.getCardType(cn, data.getCurrency());
                switch (cardType) {
                    case Helper.CARD_MADA:
                        viewCard.setImageResource(R.drawable.ic_mada);
                        break;
                    case Helper.CARD_VISA:
                        viewCard.setImageResource(R.drawable.ic_visa);
                        break;
                    case Helper.CARD_MC:
                        viewCard.setImageResource(R.drawable.ic_mastercard);
                        break;
                    case Helper.CARD_AMEX:
                        viewCard.setImageResource(R.drawable.ic_americanexpress);
                        break;
                    case Helper.CARD_MEEZA:
                        viewCard.setImageResource(R.drawable.meeza);
                        break;
                    case Helper.CARD_MAESTRO:
                        viewCard.setImageResource(R.drawable.maestro);
                        break;
                    case Helper.CARD_UNIONPAY:
                        viewCard.setImageResource(R.drawable.unionpay);
                        break;
                    case Helper.CARD_DISCOVER:
                        viewCard.setImageResource(R.drawable.dicover);
                        break;
                    case Helper.CARD_JCB:
                        viewCard.setImageResource(R.drawable.jcb);
                        break;
                    case Helper.CARD_DINERS:
                        viewCard.setImageResource(R.drawable.diners);
                        break;
                    case Helper.CARD_OMANNET:
                        viewCard.setImageResource(R.drawable.omannet);
                        break;
                    default:
                        viewCard.setImageResource(R.drawable.ic_genericcard);
                        break;
                }
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });

        edtExp.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 5; // size of pattern 00/00
            private static final int TOTAL_DIGITS = 4; // max numbers of digits in pattern: 00 x 4
            private static final int DIVIDER_MODULO = 3; // means divider position is every 2ns symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = '/';

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
                String exp = s.toString();
                validateEntry(edtCardName.getText().toString(), edtCardNumber.getText().toString(), exp, edtCVV.getText().toString());
//                if ((validationStatus & 4) > 0) {
//                    edtExp.setBackgroundResource(R.drawable.roundtextboxred);
//                } else {
//                    edtExp.setBackgroundResource(R.drawable.roundtextbox);
//                }
                enablePaymentButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // check that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }
                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }

        });

        edtCardName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String cName = s.toString();
                validateEntry(cName, edtCardNumber.getText().toString(), edtExp.getText().toString(), edtCVV.getText().toString());
//                if ((validationStatus & 1) > 0) {
//                    edtCardName.setBackgroundResource(R.drawable.roundtextboxred);
//                } else {
//                    edtCardName.setBackgroundResource(R.drawable.roundtextbox);
//                }
                enablePaymentButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        edtCVV.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String cvv = s.toString();
                validateEntry(edtCardName.getText().toString(), edtCardNumber.getText().toString(), edtExp.getText().toString(), cvv);
//                if ((validationStatus & 8) > 0) {
//                    edtCVV.setBackgroundResource(R.drawable.roundtextboxred);
//                } else {
//                    edtCVV.setBackgroundResource(R.drawable.roundtextbox);
//                }
                enablePaymentButton();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });
    }

    private String checkCardNumberValidation(String cardNumber) {
        String error = "";
        String cardType = Helper.getCardType(cardNumber, data.getCurrency());
        int clen = cardNumber.length();
        if (cardNumber.isEmpty()) {
            error = context.getResources().getString(R.string.please_enter_card_number);
        } else if ((cardType.equals(Helper.CARD_DINERS) && clen != 14 && clen != 17 && clen != 19 && clen != 23) // not support
                || (cardType.equals(Helper.CARD_MEEZA) && clen != 16 && clen != 19 && clen != 23) // not check on luhn
                || (cardType.equals(Helper.CARD_OMANNET) && clen != 16 && clen != 19 && clen != 23) //not support luhn
        ) {
            error = context.getResources().getString(R.string.invalid_card_number);

        } else if (!cardType.equals(Helper.CARD_DINERS) && !cardType.equals(Helper.CARD_MEEZA) && !cardType.equals(Helper.CARD_OMANNET)) {
            if (!validateCard(cardNumber) //check luhn algo;;;
                    || (cardType.equals(Helper.CARD_AMEX) && clen != 15 && clen != 18)
                    || (cardType.equals(Helper.CARD_MADA) && clen != 16 && clen != 19)
                    || (cardType.equals(Helper.CARD_JCB) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_DISCOVER) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_MAESTRO) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_UNIONPAY) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_MC) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_VISA) && clen != 16 && clen != 19 && clen != 23)) {
                error = context.getResources().getString(R.string.invalid_card_number);

            }
        }

        return error;
    }

    private String checkExpiryDateValidation(String exp) {
        String error = "";
        if (exp.isEmpty()) {
            error = context.getResources().getString(R.string.please_enter_expiry_date);
        } else if (exp.length() != 5) {
            error = context.getResources().getString(R.string.invalid_expiry_date);
        } else {
            //check for valid month and year
            if (!checkExp(exp)) {
                error = context.getResources().getString(R.string.invalid_expiry_date);
            }
        }

        return error;
    }


    private String cvvValidation(String cvv, String cardNumber) {
        String error = "";
        String cardType = Helper.getCardType(cardNumber, data.getCurrency());

        if (cvv.isEmpty()) {
            error = context.getResources().getString(R.string.please_enter_cvv);
        } else if ((!cardType.equals(Helper.CARD_AMEX) && cvv.length() != 3)
                || (cardType.equals(Helper.CARD_AMEX) && cvv.length() != 4)) {

            error = context.getResources().getString(R.string.invalid_cvv);
        }
        return error;
    }

    private void doPayment() {
        String cardNumber = edtCardNumber.getText().toString().trim().replace(" ", "");
        String cardName = edtCardName.getText().toString().trim();
        String exp = edtExp.getText().toString();
        String cvv = edtCVV.getText().toString();
        String cardType = "";

        //validate
        validateEntry(cardName, cardNumber, exp, cvv);
        if (!validationMessage.equals("")) {
            //make the text boxes red
            edtCardName.setBackgroundResource(((validationStatus & 1) > 0) ? R.drawable.roundtextboxred : R.drawable.roundtextbox);
            edtCardNumber.setBackgroundResource(((validationStatus & 2) > 0) ? R.drawable.roundtextboxred : R.drawable.roundtextbox);
            edtExp.setBackgroundResource(((validationStatus & 4) > 0) ? R.drawable.roundtextboxred : R.drawable.roundtextbox);
            edtCVV.setBackgroundResource(((validationStatus & 8) > 0) ? R.drawable.roundtextboxred : R.drawable.roundtextbox);
            Toast toast = Toast.makeText(this, validationMessage, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //check if the year and month are correct
        Intent in = new Intent(this, FinishActivity.class);
        in.putExtra("cardnumber", cardNumber);
        in.putExtra("cardname", cardName);
        in.putExtra("exp", exp);
        in.putExtra("cvv", cvv);
        in.putExtra("isSavedCard",isSaveCard);
        in.putExtra("addnewcard", true);
        in.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(in);
        finish();
    }

    private void enablePaymentButton() {
        if (validationStatus == 0) {
            paynowBox.setBackgroundColor(userUI.getPaynowBackgroundColorHighlight());
            paynowBox.setEnabled(true);
            txtPayNow.setTextColor(Color.WHITE);
        } else {
            paynowBox.setBackgroundColor(userUI.getPaynowBackgroundColor());
            paynowBox.setEnabled(false);
            txtPayNow.setTextColor(userUI.getPaynowForegroundColor());
        }
    }


    private void validateEntry(String cardName, String cardNumber, String exp, String cvv) {
        validationMessage = "";
        validationStatus = 0;

        cardNumber = cardNumber.replace(" ", "");
        int clen = cardNumber.length();
        String cardType = Helper.getCardType(cardNumber, data.getCurrency());
        cardName = cardName.trim();
        cvv = cvv.trim();

        if (cardName.isEmpty()) {
            validationStatus = validationStatus | 1;
            validationMessage += Helper.getLanguageTextByUser(this, "name_validate", userUI.getLanguage(), "") + "\r\n";
        }

        if (cardNumber.isEmpty()
                || (cardType.equals(Helper.CARD_MEEZA) && clen != 16 && clen != 19 && clen != 23)
                || (cardType.equals(Helper.CARD_OMANNET) && clen != 16 && clen != 19 && clen != 23)
                || (cardType.equals(Helper.CARD_DINERS) && clen != 14 && clen != 17 && clen != 19 && clen != 23)) {
            validationStatus = validationStatus | 2;
            validationMessage += Helper.getLanguageTextByUser(this, "valid_card", userUI.getLanguage(), "") + "\r\n";

        } else if (!cardType.equals(Helper.CARD_DINERS) && !cardType.equals(Helper.CARD_MEEZA) && !cardType.equals(Helper.CARD_OMANNET)) {
            if (cardNumber.isEmpty() || !validateCard(cardNumber)
                    || (cardType.equals(Helper.CARD_AMEX) && clen != 15 && clen != 18)
                    || (cardType.equals(Helper.CARD_MADA) && clen != 16)
                    || (cardType.equals(Helper.CARD_JCB) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_DISCOVER) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_MAESTRO) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_UNIONPAY) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_MC) && clen != 16 && clen != 19 && clen != 23)
                    || (cardType.equals(Helper.CARD_VISA) && clen != 16 && clen != 19 && clen != 23)) {
                validationStatus = validationStatus | 2;
                validationMessage += Helper.getLanguageTextByUser(this, "valid_card", userUI.getLanguage(), "") + "\r\n";
            }
        }

        if (exp.length() != 5) {
            validationStatus = validationStatus | 4;
            validationMessage += Helper.getLanguageTextByUser(this, "exp_validate", userUI.getLanguage(), "") + "\r\n";
        } else {
            //check for valid month and year
            if (!checkExp(exp)) {
                validationStatus = validationStatus | 4;
                validationMessage += Helper.getLanguageTextByUser(this, "exp_validate", userUI.getLanguage(), "") + "\r\n";
            }
        }

        if (cvv.isEmpty() || (!cardType.equals(Helper.CARD_AMEX) && cvv.length() != 3)
                || (cardType.equals(Helper.CARD_AMEX) && cvv.length() != 4)) {
            validationStatus = validationStatus | 8;
            validationMessage += Helper.getLanguageTextByUser(this, "cvv_validate", userUI.getLanguage(), "") + "\r\n";
        }

    }

    private boolean checkExp(String exp) {
        int expMonth = stringToInteger(exp.substring(0, 2));
        int expYear = stringToInteger(exp.substring(3, 5)) + 2000;

        if (expMonth < 1 || expMonth > 12) {
            return false;
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        return expYear >= currentYear && (expYear != currentYear || expMonth >= currentMonth);
    }

    private boolean validateCard(String cardNumber) {
        //validate card - using luhn validation
        // int array for processing the cardNumber
        cardNumber = cardNumber.replace(" ", "");
        if (cardNumber.length() < 15)
            return false;

        int[] cardIntArray = new int[cardNumber.length()];

        for (int i = 0; i < cardNumber.length(); i++) {
            char c = cardNumber.charAt(i);
            cardIntArray[i] = Integer.parseInt("" + c);
        }

        for (int i = cardIntArray.length - 2; i >= 0; i = i - 2) {
            int num = cardIntArray[i];
            num = num * 2;  // step 1
            if (num > 9) {
                num = num % 10 + num / 10;  // step 2
            }
            cardIntArray[i] = num;
        }

        int sum = Arrays.stream(cardIntArray).sum();  // step 3

        System.out.println(sum);

        // step 4
        return sum % 10 == 0;
    }

    private void doBack() {

        if (setup.getNoonSavedCards().size() == 0) {
            Intent in = new Intent(this, ModeActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(in);
            finish();
        } else {
            Intent in = new Intent(this, CardActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(in);
            finish();
        }

    }

    private void doCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewCardActivity.this);
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

    private void doCancelNoDialog() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("response", "cancel");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


    private void setTextColours() {

        //text colours
        txtBack.setTextColor(userUI.getPaymentOptionHeadingForeground());

        int textColour1 = userUI.getBoxForegroundColor();
        txtCardNumber.setTextColor(textColour1);
        txtCardName.setTextColor(textColour1);
        txtCVV.setTextColor(textColour1);
        txtExp.setTextColor(textColour1);
        txtAddNewCard.setTextColor(textColour1);
    }

    private int stringToInteger(String value) {
        try {
            int i = 0;
            i = Integer.parseInt(value);

            return i;
        } catch (Exception ex) {
            return -1;
        }
    }

}