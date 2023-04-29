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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.noonpayments.paymentsdk.Base.BaseActivity;
import com.noonpayments.paymentsdk.R;
import com.noonpayments.paymentsdk.Utils.CommonMethods;
import com.noonpayments.paymentsdk.databinding.ActivityNewCardBinding;
import com.noonpayments.paymentsdk.helpers.Helper;

import java.util.Arrays;
import java.util.Calendar;

public class NewCardActivity extends BaseActivity {

    int validationStatus = 0;
    String validationMessage = "";
    Context context;
    Boolean isSaveCard = true;
    private ActivityNewCardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(getWindow().FEATURE_NO_TITLE);
        binding = ActivityNewCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFinishOnTouchOutside(false);

        // get context to set language...
        context = setLocale(this, getLanguage());

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initView();
        initEvents();

    }

    private void initView() {
        //set text
        setText();
        //text colours
        setTextColours();
    }

    private void setText() {

        binding.paynowBox.setEnabled(false);

        if (setup.getNoonSavedCards().size() > 0)
            binding.txtBack.setText(context.getResources().getString(R.string.back_saved));
        else
            binding.txtBack.setText(context.getResources().getString(R.string.back_payment));

        binding.txtCardName.setText(context.getResources().getString(R.string.name_on_card));
        binding.txtCardNumber.setText(context.getResources().getString(R.string.card_number));
        binding.txtCVV.setText(context.getResources().getString(R.string.cvv));
        binding.txtExpiry.setText(context.getResources().getString(R.string.expiry));

        binding.txtPayNow.setText(context.getResources().getString(R.string.pay_now));
        binding.txtPay.setText(context.getResources().getString(R.string.payable_amount));

        binding.txtAddNewCard.setText(context.getResources().getString(R.string.add_card));
        binding.txtAmount.setText(data.getDisplayAmount(userUI.getLanguage()));

        //set hints
        binding.edtCardName.setHint(context.getResources().getString(R.string.name_validate));
        binding.edtCardNumber.setHint(context.getResources().getString(R.string.number_validate));
        binding.edtExpiry.setHint(context.getResources().getString(R.string.mmyy));
        binding.edtCVV.setHint(context.getResources().getString(R.string.secure_code));

        //colors & icons
        userUI.setupDialog(this, binding.dialogMode);
        userUI.setupPaynow(this, binding.paymentBox, binding.paynowBox, binding.txtPay, binding.txtAmount, binding.txtPayNow);
        userUI.setupBox(this, binding.cardEntryBox, 2);
        userUI.setupLogo(binding.viewLogo);

    }

    private void setTextColours() {
        //text colours
        binding.txtBack.setTextColor(userUI.getPaymentOptionHeadingForeground());

        int textColour1 = userUI.getBoxForegroundColor();
        binding.txtCardNumber.setTextColor(textColour1);
        binding.txtCardName.setTextColor(textColour1);
        binding.txtCVV.setTextColor(textColour1);
        binding.txtExpiry.setTextColor(textColour1);
        binding.txtAddNewCard.setTextColor(textColour1);
    }

    private void initEvents() {
        binding.paynowBox.setOnClickListener(view -> doPayment());

        binding.txtBack.setOnClickListener(view -> doBack());

        binding.viewArrowBack.setOnClickListener(view -> doBack());

        binding.btnCancel.setOnClickListener(view -> doCancel());

        binding.saveCardcb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isSaveCard = isChecked;

        });

        binding.edtCardNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String errorMsg = checkCardNumberValidation(binding.edtCardNumber.getText().toString());
                binding.txtCardError.setText(errorMsg);
                binding.txtCardError.setVisibility(View.VISIBLE);
                binding.edtCardNumber.setBackgroundResource(R.drawable.roundtextboxred);
                if (errorMsg.isEmpty()) {
                    binding.txtCardError.setVisibility(View.GONE);
                    binding.edtCardNumber.setBackgroundResource(R.drawable.roundtextbox);

                }
                if (!binding.edtCVV.getText().toString().isEmpty()) {
                    errorMsg = cvvValidation(binding.edtCVV.getText().toString(), binding.edtCardNumber.getText().toString());
                    if (errorMsg.isEmpty()) {
                        binding.edtCVV.setBackgroundResource(R.drawable.roundtextbox);
                        binding.txtCVVError.setVisibility(View.GONE);
                    } else {
                        binding.txtCVVError.setText(errorMsg);
                        binding.txtCVVError.setVisibility(View.VISIBLE);
                        binding.edtCVV.setBackgroundResource(R.drawable.roundtextboxred);
                    }
                }
            }
        });

        binding.edtExpiry.setOnFocusChangeListener(((v, hasFocus) -> {
            if (!hasFocus) {
                String errorMsg = checkExpiryDateValidation(binding.edtExpiry.getText().toString());
                binding.txtExpiryError.setText(errorMsg);
                binding.txtExpiryError.setVisibility(View.VISIBLE);
                binding.edtExpiry.setBackgroundResource(R.drawable.roundtextboxred);
                if (errorMsg.isEmpty()) {
                    binding.txtExpiryError.setVisibility(View.GONE);
                    binding.edtExpiry.setBackgroundResource(R.drawable.roundtextbox);
                }
            }
        }));

        binding.edtCVV.setOnFocusChangeListener(((v, hasFocus) -> {
            if (!hasFocus) {
                String errorMsg = cvvValidation(binding.edtCVV.getText().toString(), binding.edtCardNumber.getText().toString());
                binding.txtCVVError.setText(errorMsg);
                binding.txtCVVError.setVisibility(View.VISIBLE);
                binding.edtCVV.setBackgroundResource(R.drawable.roundtextboxred);
                if (errorMsg.isEmpty()) {
                    binding.edtCVV.setBackgroundResource(R.drawable.roundtextbox);
                    binding.txtCVVError.setVisibility(View.GONE);
                }
            }
        }));

        binding.edtCardName.setOnFocusChangeListener(((v, hasFocus) -> {
            if (!hasFocus) {
                if (binding.edtCardName.getText().toString().isEmpty()) {
                    binding.txtCardNameError.setText(context.getResources().getString(R.string.enter_name_on_card));
                    binding.txtCardNameError.setVisibility(View.VISIBLE);
                    binding.edtCardName.setBackgroundResource(R.drawable.roundtextboxred);
                } else {
                    binding.txtCardNameError.setVisibility(View.GONE);
                    binding.edtCardName.setBackgroundResource(R.drawable.roundtextbox);

                }
            }
        }));

        binding.edtCardNumber.addTextChangedListener(new TextWatcher() {
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
                validateEntry(binding.edtCardName.getText().toString(), cardNumber, binding.edtExpiry.getText().toString(),
                        binding.edtCVV.getText().toString());
                enablePaymentButton();

                if (s.length() == 0) {
                    binding.edtCVV.setText("");
                    binding.edtExpiry.setText("");
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
                        binding.viewCard.setImageResource(R.drawable.ic_mada);
                        break;
                    case Helper.CARD_VISA:
                        binding.viewCard.setImageResource(R.drawable.ic_visa);
                        break;
                    case Helper.CARD_MC:
                        binding.viewCard.setImageResource(R.drawable.ic_mastercard);
                        break;
                    case Helper.CARD_AMEX:
                        binding.viewCard.setImageResource(R.drawable.ic_americanexpress);
                        break;
                    case Helper.CARD_MEEZA:
                        binding.viewCard.setImageResource(R.drawable.meeza);
                        break;
                    case Helper.CARD_MAESTRO:
                        binding.viewCard.setImageResource(R.drawable.maestro);
                        break;
                    case Helper.CARD_UNIONPAY:
                        binding.viewCard.setImageResource(R.drawable.unionpay);
                        break;
                    case Helper.CARD_DISCOVER:
                        binding.viewCard.setImageResource(R.drawable.dicover);
                        break;
                    case Helper.CARD_JCB:
                        binding.viewCard.setImageResource(R.drawable.jcb);
                        break;
                    case Helper.CARD_DINERS:
                        binding.viewCard.setImageResource(R.drawable.diners);
                        break;
                    case Helper.CARD_OMANNET:
                        binding.viewCard.setImageResource(R.drawable.omannet);
                        break;
                    default:
                        binding.viewCard.setImageResource(R.drawable.ic_genericcard);
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

        binding.edtExpiry.addTextChangedListener(new TextWatcher() {
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
                validateEntry(binding.edtCardName.getText().toString(), binding.edtCardNumber.getText().toString(),
                        exp, binding.edtCVV.getText().toString());
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

        binding.edtCardName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String cName = s.toString();
                validateEntry(cName, binding.edtCardNumber.getText().toString(), binding.edtExpiry.getText().toString(),
                        binding.edtCVV.getText().toString());
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

        binding.edtCVV.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String cvv = s.toString();
                validateEntry(binding.edtCardName.getText().toString(), binding.edtCardNumber.getText().toString(),
                        binding.edtExpiry.getText().toString(), cvv);
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
        String cardNumber = binding.edtCardNumber.getText().toString().trim().replace(" ", "");
        String cardName = binding.edtCardName.getText().toString().trim();
        String exp = binding.edtExpiry.getText().toString();
        String cvv = binding.edtCVV.getText().toString();
        String cardType = "";

        //validate
        validateEntry(cardName, cardNumber, exp, cvv);
        if (!validationMessage.equals("")) {
            //make the text boxes red
            binding.edtCardName.setBackgroundResource(((validationStatus & 1) > 0) ? R.drawable.roundtextboxred : R.drawable.roundtextbox);
            binding.edtCardNumber.setBackgroundResource(((validationStatus & 2) > 0) ? R.drawable.roundtextboxred : R.drawable.roundtextbox);
            binding.edtExpiry.setBackgroundResource(((validationStatus & 4) > 0) ? R.drawable.roundtextboxred : R.drawable.roundtextbox);
            binding.edtCVV.setBackgroundResource(((validationStatus & 8) > 0) ? R.drawable.roundtextboxred : R.drawable.roundtextbox);
            Toast toast = Toast.makeText(this, validationMessage, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //check if the year and month are correct
        Intent in = new Intent(this, FinalActivity.class);
        in.putExtra("cardnumber", cardNumber);
        in.putExtra("cardname", cardName);
        in.putExtra("exp", exp);
        in.putExtra("cvv", cvv);
        in.putExtra("isSavedCard", isSaveCard);
        in.putExtra("addnewcard", true);
        in.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(in);
        finish();
    }

    private void enablePaymentButton() {
        if (validationStatus == 0) {
            binding.paynowBox.setBackgroundColor(userUI.getPaynowBackgroundColorHighlight());
            binding.paynowBox.setEnabled(true);
            binding.txtPayNow.setTextColor(Color.WHITE);
        } else {
            binding.paynowBox.setBackgroundColor(userUI.getPaynowBackgroundColor());
            binding.paynowBox.setEnabled(false);
            binding.txtPayNow.setTextColor(userUI.getPaynowForegroundColor());
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
            if (!validateCard(cardNumber) || cardType.equals(Helper.CARD_AMEX) && clen != 15 && clen != 18 || cardType.equals(Helper.CARD_MADA) && clen != 16 || cardType.equals(Helper.CARD_JCB) && clen != 16 && clen != 19 && clen != 23 || cardType.equals(Helper.CARD_DISCOVER) && clen != 16 && clen != 19 && clen != 23 || cardType.equals(Helper.CARD_MAESTRO) && clen != 16 && clen != 19 && clen != 23 || cardType.equals(Helper.CARD_UNIONPAY) && clen != 16 && clen != 19 && clen != 23 || cardType.equals(Helper.CARD_MC) && clen != 16 && clen != 19 && clen != 23 || cardType.equals(Helper.CARD_VISA) && clen != 16 && clen != 19 && clen != 23) {
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
        int expMonth = CommonMethods.INSTANCE.stringToInteger(exp.substring(0, 2));
        int expYear = CommonMethods.INSTANCE.stringToInteger(exp.substring(3, 5)) + 2000;

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
}