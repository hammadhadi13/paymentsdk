package com.noonpayments.paymentsdk.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;

import com.noonpayments.paymentsdk.R;
import com.noonpayments.paymentsdk.helpers.Helper;
import com.noonpayments.paymentsdk.models.NoonPaymentsAPIConfig;
import com.noonpayments.paymentsdk.models.NoonPaymentsData;
import com.noonpayments.paymentsdk.models.NoonPaymentsResponse;
import com.noonpayments.paymentsdk.models.NoonPaymentsSetup;
import com.noonpayments.paymentsdk.models.NoonPaymentsUI;
import com.noonpayments.paymentsdk.models.PaymentMode;
import com.noonpayments.paymentsdk.models.PaymentType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FinishActivity extends BaseActivity {
    NoonPaymentsSetup setup;
    NoonPaymentsUI userUI;
    NoonPaymentsData data;
    NoonPaymentsResponse noonPaymentsResponse;
    RelativeLayout dialogMode;
    TextView txtMessage1, txtMessage2, txtPaySecure;
    ImageView viewStatus, viewLogo;
    ImageButton btnCancel;
    ProgressBar progressBar;

    //payment information
    boolean addNewCard = false;
    boolean isSavedCard = false;
    String cardNumber = "";
    String cardName = "";
    String expMonth = "";
    String expYear = "";
    String cvv = "";
    String cardToken = "";

    //response information
    boolean is3DS = false;
    String responseJson = "";
    String responseMessage = "";
    String responseCardToken = "";
    String responseCardNumber = "";
    String responseCardType = "";
    String responseTransactionId = "";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final OkHttpClient client = new OkHttpClient();
    NoonPaymentsAPIConfig noonPaymentsAPIConfig = new NoonPaymentsAPIConfig();

    Handler mainHandler;
    Context context;

    ActivityResultLauncher<Intent> finishActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_finish);
        setFinishOnTouchOutside(false);

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
        noonPaymentsResponse = new NoonPaymentsResponse();

        Bundle extras = getIntent().getExtras();
        addNewCard = extras.getBoolean("addnewcard");
        isSavedCard = extras.getBoolean("isSavedCard");
        if (addNewCard) {
            cardName = extras.getString("cardname");
            cardNumber = extras.getString("cardnumber");
            String exp = extras.getString("exp");
            expMonth = exp.substring(0, 2);
            expYear = "20" + exp.substring(3, 5);
            cvv = extras.getString("cvv");
        } else {
            cardToken = extras.getString("cardtoken");
            cvv = extras.getString("cvv");
        }

        initView();

        initEvents();

        mainHandler = new Handler(this.getMainLooper());

        //start processing
        processPayment();

        onActivityResultFunction();
    }

    private void initView() {
        dialogMode = (RelativeLayout) findViewById(R.id.dialogMode);
        txtMessage1 = (TextView) findViewById(R.id.txtMessage1);
        txtMessage2 = (TextView) findViewById(R.id.txtMessage2);
        txtPaySecure = (TextView) findViewById(R.id.txtPaySecure);
        viewLogo = (ImageView) findViewById(R.id.viewLogo);

        viewStatus = (ImageView) findViewById(R.id.viewSuccess);
        progressBar = (ProgressBar) findViewById(R.id.pgrBar);

        btnCancel = (ImageButton) findViewById(R.id.btnCancel);

        viewStatus.setVisibility(View.GONE);

        txtPaySecure.setText(context.getResources().getString(R.string.payment_secure));
        txtMessage1.setText(context.getResources().getString(R.string.payment_processing));
        txtMessage2.setText("");

        //colors & icons
        userUI.setupDialog(this, dialogMode);
        userUI.setupLogo(viewLogo);
        txtPaySecure.setTextColor(userUI.getFooterForegroundColor());
        txtMessage1.setTextColor(userUI.getPaymentOptionHeadingForeground());
        txtMessage2.setTextColor(userUI.getPaymentOptionHeadingForeground());
    }

    private void initEvents() {
        btnCancel.setOnClickListener(view -> doComplete());
    }

    private void do3DSflow(String url3ds) {
        Intent in = new Intent(FinishActivity.this, DSActivity.class);
        in.putExtra("url3ds", url3ds);
//        startActivityForResult(in, 4000);
        finishActivityLauncher.launch(in);
    }

     void onActivityResultFunction(){
        finishActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            String rOrderId = result.getData().getStringExtra("orderid");
                            String rMerchantId = result.getData().getStringExtra("merchantid");

                            if (rOrderId.isEmpty() || rMerchantId.isEmpty() || !rOrderId.equals(data.getOrderId())) {
                                //payment failure
                                String message = Helper.getLanguageTextByUser(FinishActivity.this, "payment_error_reference", userUI.getLanguage(), "");
                                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", responseJson);
                                displayResult(false, context.getResources().getString(R.string.payment_failed),
                                        message);
                            } else {
                                //validate the order by calling inquiry
                                validateTransacton("");
                            }
                        }
                    }
                }
        );
    }

    private void doComplete() {
        //setup according to api calls etc....
        Intent resultIntent = new Intent();
        resultIntent.putExtra("noonresponse", noonPaymentsResponse);
        resultIntent.putExtra("response", "complete");

        NoonPaymentsResponse r = (NoonPaymentsResponse) resultIntent.getSerializableExtra("noonresponse");
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void processPayment() {
        try {

            //set all values to default
            noonPaymentsResponse = new NoonPaymentsResponse();
            responseJson = "";
            responseMessage = "";
            responseCardToken = "";
            responseTransactionId = "";

            String signature = createSignature();
            String json = createAddPaymentJSON(signature);
            String url = NoonPaymentsAPIConfig.NOON_URL_LIVE_ORDER;
            if (data.getPaymentMode() == PaymentMode.TEST)
                url = NoonPaymentsAPIConfig.NOON_URL_TEST_ORDER;
            post(url, json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void post(String url, String json) throws IOException {

        RequestBody body = RequestBody.create(json, JSON);
        String header = data.getAuthorizationHeader();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", header)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Something went wrong
                String message = e.getMessage();
                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", "");
                displayResult(false, context.getResources().getString(R.string.payment_failed), message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    processResponse(responseStr);
                } else {
                    // Request not successful
                    String message = response.message();
                    noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", "");
                    displayResult(false, context.getResources().getString(R.string.payment_failed), message);
                }
            }
        });
    }

    private void get(String url) {
        String header = data.getAuthorizationHeader();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", header)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Something went wrong
                String message = e.getMessage();
                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", "");
                displayResult(false, context.getResources().getString(R.string.payment_failed), message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    validateOrder(responseStr);
                } else {
                    // Request not successful
                    String message = response.message();
                    noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", "");
                    displayResult(false, context.getResources().getString(R.string.payment_failed), message);
                }
            }
        });
    }

    private void processResponse(String response) {
        try {
            boolean success = false;
            int resultCode = -1;

            responseJson = response;
            JSONObject jsonObject = new JSONObject(response);
            resultCode = jsonObject.getInt("resultCode");
            responseMessage = getJSONString(jsonObject, "message");

            if (resultCode == 0) {
                if (jsonObject.getJSONObject("result").has("nextActions")
                        && jsonObject.getJSONObject("result").getString("nextActions").equals("CHECK_3DS_ENROLLMENT")) {
                    //get post url and open browser
                    String url3DS = getJSONString(jsonObject.getJSONObject("result").getJSONObject("checkoutData"), "postUrl");
                    if (url3DS.isEmpty() == false) {
                        do3DSflow(url3DS);
                    } else {
                        String responseMessage = Helper.getLanguageTextByUser(this, "3ds_failed", userUI.getLanguage(), "");
                        noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, responseMessage, "", response);
                        displayResult(false, context.getResources().getString(R.string.payment_failed), responseMessage);
                    }
                } else {
                    //NON 3DS flow
                    responseTransactionId = getJSONString(jsonObject.getJSONObject("result").getJSONObject("transaction"), "id");
                    validateTransacton(response);
                }
            } else {
                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, responseMessage, "", response);
                displayResult(false, context.getResources().getString(R.string.payment_failed),
                        responseMessage);
            }

        } catch (JSONException e) {
            noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, "Exception: " + e.getMessage(), "", response);
            displayResult(false, context.getResources().getString(R.string.payment_failed),
                    "");
            e.printStackTrace();
        }
    }

    private boolean validateOrder(String response) {
        boolean isValid = false;

        try {
            String status = Helper.STATUS_FAILURE;
            String message = "";
            JSONObject jsonObject = new JSONObject(response);

            int resultCode = jsonObject.getInt("resultCode");
            message = getJSONString(jsonObject, "message");

            if (resultCode == 0) {
                //check amount and status
                JSONObject jsonOrder = jsonObject.getJSONObject("result").getJSONObject("order");
                String orderStatus = getJSONString(jsonOrder, "status");
                String orderReference = getJSONString(jsonOrder, "reference");
                Double amount = (data.getPaymentType().equalsIgnoreCase(PaymentType.SALE.toString())) ? getJSONDouble(jsonOrder, "totalSalesAmount") : getJSONDouble(jsonOrder, "totalAuthorizedAmount");

                JSONArray jsonTransactions = jsonObject.getJSONObject("result").getJSONArray("transactions");
                String transactionStatus = "";
                if (jsonTransactions.length() > 0) {
                    transactionStatus = getJSONString(jsonTransactions.getJSONObject(0), "status");
                }
                if (orderStatus.equalsIgnoreCase("CANCELLED")) {
                    status = Helper.STATUS_CANCELLED;
                    message = Helper.getLanguageTextByUser(this, "payment_cancelled", userUI.getLanguage(), "");
                } else if (orderStatus.equalsIgnoreCase("FAILED") || orderStatus.toUpperCase().equals("EXPIRED") || orderStatus.toUpperCase().equals("REJECTED")) {
                    status = Helper.STATUS_FAILURE;
                    message = context.getResources().getString(R.string.payment_failed);
                } else {
                    if (transactionStatus.equalsIgnoreCase("SUCCESS")) {
                        if ((data.getPaymentType().equalsIgnoreCase(PaymentType.SALE.toString()) && amount >= data.getAmount())
                                || (data.getPaymentType().equalsIgnoreCase(PaymentType.AUTHORIZE.toString()) && amount >= data.getAmount())
                                || (data.getPaymentType().equalsIgnoreCase("Authorize,Sale") && amount >= data.getAmount())) {
                            status = Helper.STATUS_SUCCESS;
                            message = context.getResources().getString(R.string.payment_success);

                            responseTransactionId = getJSONString(jsonObject.getJSONObject("result").getJSONArray("transactions").getJSONObject(0), "id");

                            //set the token values
                            if (data.isAllowCardTokenization()) {
                                if (jsonObject.getJSONObject("result").has("paymentDetails") &&
                                        jsonObject.getJSONObject("result").getJSONObject("paymentDetails").has("tokenIdentifier")) {
                                    responseCardToken = getJSONString(jsonObject.getJSONObject("result").getJSONObject("paymentDetails"), "tokenIdentifier");
                                    responseCardNumber = getJSONString(jsonObject.getJSONObject("result").getJSONObject("paymentDetails"), "paymentInfo");
                                    String cn = getJSONString(jsonObject.getJSONObject("result").getJSONObject("paymentDetails"), "paymentInfo");
                                    responseCardType = Helper.getCardType(cn, data.getCurrency());
                                }
                            }

                        } else {
                            status = Helper.STATUS_FAILURE;
                            message = Helper.getLanguageTextByUser(this, "payment_error_amount", userUI.getLanguage(), "");
                        }
                    } else {
                        status = Helper.STATUS_FAILURE;
                        message = Helper.getLanguageTextByUser(this, "payment_cancelled", userUI.getLanguage(), "");
                    }
                }
            }

            if (status.equals(Helper.STATUS_SUCCESS)) {
                //display results
                if (data.isAllowCardTokenization() && !responseCardToken.isEmpty()) {
                    noonPaymentsResponse.setDetails(Helper.STATUS_SUCCESS, message, responseTransactionId, response, responseCardNumber, responseCardType, responseCardToken);
                } else {
                    noonPaymentsResponse.setDetails(Helper.STATUS_SUCCESS, message, responseTransactionId, response);
                }
                displayResult(true, data.getDisplayAmount(userUI.getLanguage()),
                        context.getResources().getString(R.string.payment_success));
            } else {
                if (message.isEmpty())
                    message = context.getResources().getString(R.string.payment_failed);

                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", response);
                displayResult(false, context.getResources().getString(R.string.payment_failed),
                        message);
            }

        } catch (JSONException e) {
            noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, e.getMessage(), "", response);
            displayResult(false, context.getResources().getString(R.string.payment_failed), e.getMessage());
            e.printStackTrace();
        }
        return isValid;
    }

    private void validateTransacton(String response) {
        String url = NoonPaymentsAPIConfig.NOON_URL_LIVE_ORDER;
        if (data.getPaymentMode() == PaymentMode.TEST)
            url = NoonPaymentsAPIConfig.NOON_URL_TEST_ORDER;
        url += data.getOrderId();
        get(url);
    }

    private void displayResult(boolean success, String message1, String message2) {
        mainHandler.post(() -> {
            // do your work on main thread
            if (success) {
                viewStatus.setImageResource(R.drawable.ic_success);
                txtMessage1.setText(message1);
                txtMessage2.setText(message2);
            } else {
                viewStatus.setImageResource(R.drawable.ic_failure);
                txtMessage1.setText(message1);
                txtMessage2.setText("");
            }
            progressBar.setVisibility(View.GONE);
            viewStatus.setVisibility(View.VISIBLE);

        });
    }

    private String createAddPaymentJSON(String signature) {
        try {
            JSONObject initObject = new JSONObject();
            initObject.put("apiOperation", "ADD_PAYMENT_INFO");

            JSONObject orderObject = new JSONObject();
            orderObject.put("id", data.getOrderId());

            JSONObject paymentObject = new JSONObject();
            paymentObject.put("type", "CARD");

            JSONObject cardObject = new JSONObject();

            JSONObject conObj = new JSONObject();

            if (addNewCard) {
                cardObject.put("nameOnCard", cardName);
                cardObject.put("numberPlain", cardNumber);
                cardObject.put("cvv", cvv);
                cardObject.put("expiryMonth", expMonth);
                cardObject.put("expiryYear", expYear);
                cardObject.put("sdkSignature", signature);
            } else {
                cardObject.put("cvv", cvv);
                cardObject.put("sdkSignature", signature);
                cardObject.put("tokenIdentifier", cardToken);
            }

            paymentObject.put("data", cardObject);

            conObj.put("payerConsentForToken", isSavedCard);

            initObject.put("order", orderObject);
            initObject.put("paymentData", paymentObject);

            initObject.put("configuration", conObj);

            return initObject.toString();
        } catch (Exception ex) {
            Log.e("NoonSDK", ex.getMessage());
            Log.e("NoonSDK", ex.getStackTrace().toString());
            return "";
        }
    }

    private String createSignature() {
        String sha512 = data.getOrderId() + "|" + data.getPaymentCategory() + "|" + data.getCurrency() +
                "|" + data.getPaymentChannel() + "|ADD_PAYMENT_INFO|noonpayments_sdk";
        sha512 = sha512.toLowerCase();
        String hash = hashString(sha512);
        String signature = encrypt(hash, data.getPublicKey());

        return signature;
    }

    private String hashString(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 128) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception ex) {
            return "";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encrypt(String data, String publicKey) {

        try {
            publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----\n", "");
            publicKey = publicKey.replace("\n-----END PUBLIC KEY-----\n", "");
            publicKey = publicKey.replace("\n", "");

            byte[] publicBytes = java.util.Base64.getDecoder().decode(publicKey.getBytes());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");        //"RSA/ECB/PKCS1Padding"
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encryptedMessage = cipher.doFinal(data.getBytes());
            String b64 = java.util.Base64.getEncoder().encodeToString(encryptedMessage);

            return b64;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultdata) {
        super.onActivityResult(requestCode, resultCode, resultdata);
        // check if the request code is same as what is passed  here it is 2
        if (resultCode == Activity.RESULT_OK && requestCode == 4000) {
            String rOrderId = resultdata.getStringExtra("orderid");
            String rMerchantId = resultdata.getStringExtra("merchantid");

            if (rOrderId.isEmpty() || rMerchantId.isEmpty() || !rOrderId.equals(data.getOrderId())) {
                //payment failure
                String message = Helper.getLanguageTextByUser(this, "payment_error_reference", userUI.getLanguage(), "");
                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", responseJson);
                displayResult(false, context.getResources().getString(R.string.payment_failed),
                        message);
            } else {
                //validate the order by calling inquiry
                validateTransacton("");
            }
        }
    }

    private String getJSONString(JSONObject jObject, String key) {
        String value = "";
        try {
            if (jObject.has(key))
                value = jObject.getString(key);
        } catch (Exception ex) {
            String qqqq = key;
        }

        return value;
    }

    private double getJSONDouble(JSONObject jObject, String key) {
        double value = 0.0;
        try {
            if (jObject.has(key))
                value = jObject.getDouble(key);
        } catch (Exception ex) {
        }

        return value;
    }
}