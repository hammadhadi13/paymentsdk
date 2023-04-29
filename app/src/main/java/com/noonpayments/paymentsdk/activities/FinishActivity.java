//package com.noonpayments.paymentsdk.activities;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.Observer;
//
//import com.arhamsoft.ilets.domain.repositories.GetApiResponseRepo;
//import com.google.gson.Gson;
//import com.noonpayments.paymentsdk.Base.BaseActivity;
//import com.noonpayments.paymentsdk.R;
//import com.noonpayments.paymentsdk.Utils.CommonMethods;
//import com.noonpayments.paymentsdk.Utils.URLs;
//import com.noonpayments.paymentsdk.ViewModel.ApiCallingViewModel;
//import com.noonpayments.paymentsdk.databinding.ActivityFinishBinding;
//import com.noonpayments.paymentsdk.helpers.Helper;
//import com.noonpayments.paymentsdk.models.NoonPaymentsAPIConfig;
//import com.noonpayments.paymentsdk.models.NoonPaymentsResponse;
//import com.noonpayments.paymentsdk.models.PaymentMode;
//import com.noonpayments.paymentsdk.models.PaymentStatus;
//import com.noonpayments.paymentsdk.models.PaymentType;
//import com.noonpayments.paymentsdk.models.ResponseModel;
//import com.retech.yapiee.domain.retrofit.RetrofitClient;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.math.BigInteger;
//import java.security.MessageDigest;
//
//import kotlin.Unit;
//import kotlin.coroutines.Continuation;
//import kotlin.coroutines.CoroutineContext;
//import kotlinx.coroutines.BuildersKt;
//import kotlinx.coroutines.CoroutineScope;
//import kotlinx.coroutines.CoroutineStart;
//import kotlinx.coroutines.Dispatchers;
//import kotlinx.coroutines.GlobalScope;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import retrofit2.http.Url;
//
//public class FinishActivity extends BaseActivity {
//
//    //payment information
//    boolean addNewCard = false;
//    boolean isSavedCard = false;
//    String cardNumber = "";
//    String cardName = "";
//    String expMonth = "";
//    String expYear = "";
//    String cvv = "";
//    String cardToken = "";
//
//    //response information
//    boolean is3DS = false;
//    String responseJson = "";
//    String responseMessage = "";
//    String responseCardToken = "";
//    String responseCardNumber = "";
//    String responseCardType = "";
//    String responseTransactionId = "";
//
//    Handler mainHandler;
//    Context context;
//    ActivityResultLauncher<Intent> finishActivityLauncher;
//    private ActivityFinishBinding binding;
//    private ApiCallingViewModel apiCallingViewModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(getWindow().FEATURE_NO_TITLE);
//        binding = ActivityFinishBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        setFinishOnTouchOutside(false);
//
//        context = setLocale(this, getLanguage());
//        apiCallingViewModel = new ApiCallingViewModel(new GetApiResponseRepo(RetrofitClient.Companion.getInstance()));
//        Window window = getWindow();
//        WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.gravity = Gravity.BOTTOM;
//        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        window.setAttributes(wlp);
//        //window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//
//        Bundle extras = getIntent().getExtras();
//        addNewCard = extras.getBoolean("addnewcard");
//        isSavedCard = extras.getBoolean("isSavedCard");
//        if (addNewCard) {
//            cardName = extras.getString("cardname");
//            cardNumber = extras.getString("cardnumber");
//            String exp = extras.getString("exp");
//            expMonth = exp.substring(0, 2);
//            expYear = "20" + exp.substring(3, 5);
//            cvv = extras.getString("cvv");
//        } else {
//            cardToken = extras.getString("cardtoken");
//            cvv = extras.getString("cvv");
//        }
//
//        initView();
//
//        initEvents();
//        setObserver();
//
//        mainHandler = new Handler(this.getMainLooper());
//        //start processing
//        processPayment();
//
//        onActivityResultFunction();
//    }
//
//    private void initView() {
//
//        binding.viewSuccess.setVisibility(View.GONE);
//
//        binding.txtPaySecure.setText(context.getResources().getString(R.string.payment_secure));
//        binding.txtMessage1.setText(context.getResources().getString(R.string.payment_processing));
//        binding.txtMessage2.setText("");
//
//        //colors & icons
//        userUI.setupDialog(this, binding.dialogMode);
//        userUI.setupLogo(binding.viewLogo);
//        binding.txtPaySecure.setTextColor(userUI.getFooterForegroundColor());
//        binding.txtMessage1.setTextColor(userUI.getPaymentOptionHeadingForeground());
//        binding.txtMessage2.setTextColor(userUI.getPaymentOptionHeadingForeground());
//    }
//
//    private void initEvents() {
//        binding.btnCancel.setOnClickListener(view -> doComplete());
//    }
//
//    private void do3DSflow(String url3ds) {
//        Intent in = new Intent(FinishActivity.this, DSActivity.class);
//        in.putExtra("url3ds", url3ds);
////        startActivityForResult(in, 4000);
//        finishActivityLauncher.launch(in);
//    }
//
//    private void setObserver() {
//        apiCallingViewModel.getErrorResponse().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(String s) {
//                Toast.makeText(FinishActivity.this, "", Toast.LENGTH_SHORT).show();
//            }
//        });
//        apiCallingViewModel.getPaymentResponse().observe(this, new Observer<ResponseModel>() {
//            @Override
//            public void onChanged(ResponseModel responseModel) {
//
//            }
//        });
//
//    }
//
//    void onActivityResultFunction() {
//        finishActivityLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//
//                            assert result.getData() != null;
//                            String rOrderId = result.getData().getStringExtra("orderid");
//                            String rMerchantId = result.getData().getStringExtra("merchantid");
//
//                            if (rOrderId.isEmpty() || rMerchantId.isEmpty() || !rOrderId.equals(data.getOrderId())) {
//                                //payment failure
//                                String message = Helper.getLanguageTextByUser(FinishActivity.this, "payment_error_reference", userUI.getLanguage(), "");
//                                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", responseJson);
//                                displayResult(false, context.getResources().getString(R.string.payment_failed),
//                                        message);
//                            } else {
//                                //validate the order by calling inquiry
//                                validateTransacton("");
//                            }
//                        }
//                    }
//                }
//        );
//    }
//
//    private void doComplete() {
//        //setup according to api calls etc....
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra("noonresponse", new Gson().toJson(noonPaymentsResponse));
//        resultIntent.putExtra("response", "complete");
//        setResult(Activity.RESULT_OK, resultIntent);
//        finish();
//    }
//
//    private void processPayment() {
//        try {
//            //set all values to default
//            noonPaymentsResponse = new NoonPaymentsResponse();
//            responseJson = "";
//            responseMessage = "";
//            responseCardToken = "";
//            responseTransactionId = "";
//
//            String signature = createSignature();
//            String json = createAddPaymentJSON(signature);
//            String url = NoonPaymentsAPIConfig.NOON_URL_LIVE_ORDER;
//            if (data.getPaymentMode() == PaymentMode.TEST)
//                url = NoonPaymentsAPIConfig.NOON_URL_TEST_ORDER;
//
//            URLs.INSTANCE.setFinalBaseUrl(url);
//            BuildersKt.launch((CoroutineScope) GlobalScope.INSTANCE,
//                    (CoroutineContext) Dispatchers.getMain(),//context to be ran on
//                    CoroutineStart.DEFAULT,
//                    (coroutineScope, continuation) -> apiCallingViewModel.callPaymentApi(json, new Continuation<Unit>() {
//                        @NonNull
//                        @Override
//                        public CoroutineContext getContext() {
//                            return null;
//                        }
//
//                        @Override
//                        public void resumeWith(@NonNull Object o) {
//
//                        }
//                    })
//            );
//
//            postApi(url, json);
//        } catch (Exception ex) {
////            ex.printStackTrace();
//        }
//    }
//
//    private void postApi(String url, String json) throws IOException {
//
//        RequestBody body = RequestBody.create(json, JSON);
//        String header = data.getAuthorizationHeader();
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Authorization", header)
//                .post(body)
//                .build();
//
//        URLs.INSTANCE.getBaseClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // Something went wrong
//                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, e.getMessage(), "", "");
//                displayResult(false, context.getResources().getString(R.string.payment_failed), e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseStr = response.body().string();
//                    processResponse(responseStr);
//                } else {
//                    // Request not successful
//                    noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, response.message(), "", "");
//                    displayResult(false, context.getResources().getString(R.string.payment_failed), response.message());
//                }
//            }
//        });
//    }
//
//    private void get(String url) {
//        String header = data.getAuthorizationHeader();
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Authorization", header)
//                .build();
//
//        URLs.INSTANCE.getBaseClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // Something went wrong
//                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, e.getMessage(), "", "");
//                displayResult(false, context.getResources().getString(R.string.payment_failed), e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseStr = response.body().string();
//                    validateOrder(responseStr);
//                } else {
//                    // Request not successful
//                    noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, response.message(), "", "");
//                    displayResult(false, context.getResources().getString(R.string.payment_failed), response.message());
//                }
//            }
//        });
//    }
//
//    private void processResponse(String response) {
//        try {
//            boolean success = false;
//            int resultCode = -1;
//
//            responseJson = response;
//            JSONObject jsonObject = new JSONObject(response);
//            resultCode = jsonObject.getInt("resultCode");
//            responseMessage = CommonMethods.INSTANCE.getJSONString(jsonObject, "message");
//
//            if (resultCode == 0) {
//                if (jsonObject.getJSONObject("result").has("nextActions")
//                        && jsonObject.getJSONObject("result").getString("nextActions").equals("CHECK_3DS_ENROLLMENT")) {
//                    //get post url and open browser
//                    String url3DS = CommonMethods.INSTANCE.getJSONString(jsonObject.getJSONObject("result").getJSONObject("checkoutData"), "postUrl");
//                    if (url3DS.isEmpty() == false) {
//                        do3DSflow(url3DS);
//                    } else {
//                        String responseMessage = Helper.getLanguageTextByUser(this, "3ds_failed", userUI.getLanguage(), "");
//                        noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, responseMessage, "", response);
//                        displayResult(false, context.getResources().getString(R.string.payment_failed), responseMessage);
//                    }
//                } else {
//                    //NON 3DS flow
//                    responseTransactionId = CommonMethods.INSTANCE.getJSONString(jsonObject.getJSONObject("result").getJSONObject("transaction"), "id");
//                    validateTransacton(response);
//                }
//            } else {
//                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, responseMessage, "", response);
//                displayResult(false, context.getResources().getString(R.string.payment_failed),
//                        responseMessage);
//            }
//
//        } catch (JSONException e) {
//            noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, "Exception: " + e.getMessage(), "", response);
//            displayResult(false, context.getResources().getString(R.string.payment_failed),
//                    "");
////            e.printStackTrace();
//        }
//    }
//
//    private boolean validateOrder(String response) {
//        boolean isValid = false;
//
//        try {
//            String status = Helper.STATUS_FAILURE;
//            String message = "";
//            JSONObject jsonObject = new JSONObject(response);
//
//            int resultCode = jsonObject.getInt("resultCode");
//            message = CommonMethods.INSTANCE.getJSONString(jsonObject, "message");
//
//            if (resultCode == 0) {
//                //check amount and status
//                JSONObject jsonOrder = jsonObject.getJSONObject("result").getJSONObject("order");
//                String orderStatus = CommonMethods.INSTANCE.getJSONString(jsonOrder, "status");
//                String orderReference = CommonMethods.INSTANCE.getJSONString(jsonOrder, "reference");
//                Double amount = (data.getPaymentType().equalsIgnoreCase(PaymentType.SALE.toString())) ? CommonMethods.INSTANCE.getJSONDouble(jsonOrder, "totalSalesAmount") : CommonMethods.INSTANCE.getJSONDouble(jsonOrder, "totalAuthorizedAmount");
//
//                JSONArray jsonTransactions = jsonObject.getJSONObject("result").getJSONArray("transactions");
//                String transactionStatus = "";
//                if (jsonTransactions.length() > 0) {
//                    transactionStatus = CommonMethods.INSTANCE.getJSONString(jsonTransactions.getJSONObject(0), "status");
//                }
//                if (orderStatus.equalsIgnoreCase(PaymentStatus.CANCELLED.toString())) {
//                    status = Helper.STATUS_CANCELLED;
//                    message = Helper.getLanguageTextByUser(this, "payment_cancelled", userUI.getLanguage(), "");
//                } else if (orderStatus.equalsIgnoreCase(PaymentStatus.FAILED.toString()) ||
//                        orderStatus.toUpperCase().equals(PaymentStatus.EXPIRED.toString()) ||
//                        orderStatus.toUpperCase().equals(PaymentStatus.REJECTED.toString())) {
//                    status = Helper.STATUS_FAILURE;
//                    message = context.getResources().getString(R.string.payment_failed);
//                } else {
//                    if (transactionStatus.equalsIgnoreCase(PaymentStatus.SUCCESS.toString())) {
//                        if ((data.getPaymentType().equalsIgnoreCase(PaymentType.SALE.toString()) && amount >= data.getAmount())
//                                || (data.getPaymentType().equalsIgnoreCase(PaymentType.AUTHORIZE.toString()) && amount >= data.getAmount())
//                                || (data.getPaymentType().equalsIgnoreCase("Authorize,Sale") && amount >= data.getAmount())) {
//                            status = Helper.STATUS_SUCCESS;
//                            message = context.getResources().getString(R.string.payment_success);
//
//                            responseTransactionId = CommonMethods.INSTANCE.getJSONString(jsonObject.getJSONObject("result").getJSONArray("transactions").getJSONObject(0), "id");
//
//                            //set the token values
//                            if (data.isAllowCardTokenization()) {
//                                if (jsonObject.getJSONObject("result").has("paymentDetails") &&
//                                        jsonObject.getJSONObject("result").getJSONObject("paymentDetails").has("tokenIdentifier")) {
//                                    responseCardToken = CommonMethods.INSTANCE.getJSONString(jsonObject.getJSONObject("result").getJSONObject("paymentDetails"), "tokenIdentifier");
//                                    responseCardNumber = CommonMethods.INSTANCE.getJSONString(jsonObject.getJSONObject("result").getJSONObject("paymentDetails"), "paymentInfo");
//                                    String cn = CommonMethods.INSTANCE.getJSONString(jsonObject.getJSONObject("result").getJSONObject("paymentDetails"), "paymentInfo");
//                                    responseCardType = Helper.getCardType(cn, data.getCurrency());
//                                }
//                            }
//
//                        } else {
//                            status = Helper.STATUS_FAILURE;
//                            message = Helper.getLanguageTextByUser(this, "payment_error_amount", userUI.getLanguage(), "");
//                        }
//                    } else {
//                        status = Helper.STATUS_FAILURE;
//                        message = Helper.getLanguageTextByUser(this, "payment_cancelled", userUI.getLanguage(), "");
//                    }
//                }
//            }
//
//            if (status.equals(Helper.STATUS_SUCCESS)) {
//                //display results
//                if (data.isAllowCardTokenization() && !responseCardToken.isEmpty()) {
//                    noonPaymentsResponse.setDetails(Helper.STATUS_SUCCESS, message, responseTransactionId, response, responseCardNumber, responseCardType, responseCardToken);
//                } else {
//                    noonPaymentsResponse.setDetails(Helper.STATUS_SUCCESS, message, responseTransactionId, response);
//                }
//                displayResult(true, data.getDisplayAmount(userUI.getLanguage()),
//                        context.getResources().getString(R.string.payment_success));
//            } else {
//                if (message.isEmpty())
//                    message = context.getResources().getString(R.string.payment_failed);
//
//                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", response);
//                displayResult(false, context.getResources().getString(R.string.payment_failed),
//                        message);
//            }
//
//        } catch (JSONException e) {
//            noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, e.getMessage(), "", response);
//            displayResult(false, context.getResources().getString(R.string.payment_failed), e.getMessage());
////            e.printStackTrace();
//        }
//        return isValid;
//    }
//
//    private void validateTransacton(String response) {
//        String url = NoonPaymentsAPIConfig.NOON_URL_LIVE_ORDER;
//        if (data.getPaymentMode() == PaymentMode.TEST)
//            url = NoonPaymentsAPIConfig.NOON_URL_TEST_ORDER;
//        url += data.getOrderId();
//        get(url);
//    }
//
//    private void displayResult(boolean success, String message1, String message2) {
//        mainHandler.post(() -> {
//            // do your work on main thread
//            if (success) {
//                binding.viewSuccess.setImageResource(R.drawable.ic_success);
//                binding.txtMessage1.setText(message1);
//                binding.txtMessage2.setText(message2);
//            } else {
//                binding.viewSuccess.setImageResource(R.drawable.ic_failure);
//                binding.txtMessage1.setText(message1);
//                binding.txtMessage2.setText("");
//            }
//            binding.pgrBar.setVisibility(View.GONE);
//            binding.viewSuccess.setVisibility(View.VISIBLE);
//
//        });
//    }
//
//    private String createAddPaymentJSON(String signature) {
//        try {
//            JSONObject initObject = new JSONObject();
//            initObject.put("apiOperation", "ADD_PAYMENT_INFO");
//
//            JSONObject orderObject = new JSONObject();
//            orderObject.put("id", data.getOrderId());
//
//            JSONObject paymentObject = new JSONObject();
//            paymentObject.put("type", "CARD");
//
//            JSONObject cardObject = new JSONObject();
//
//            JSONObject conObj = new JSONObject();
//
//            if (addNewCard) {
//                cardObject.put("nameOnCard", cardName);
//                cardObject.put("numberPlain", cardNumber);
//                cardObject.put("cvv", cvv);
//                cardObject.put("expiryMonth", expMonth);
//                cardObject.put("expiryYear", expYear);
//                cardObject.put("sdkSignature", signature);
//            } else {
//                cardObject.put("cvv", cvv);
//                cardObject.put("sdkSignature", signature);
//                cardObject.put("tokenIdentifier", cardToken);
//            }
//
//            paymentObject.put("data", cardObject);
//
//            conObj.put("payerConsentForToken", isSavedCard);
//
//            initObject.put("order", orderObject);
//            initObject.put("paymentData", paymentObject);
//
//            initObject.put("configuration", conObj);
//
//            return initObject.toString();
//        } catch (Exception ex) {
//            Log.e("NoonSDK", ex.getMessage());
//            Log.e("NoonSDK", ex.getStackTrace().toString());
//            return "";
//        }
//    }
//
//    private String createSignature() {
//        String sha512 = data.getOrderId() + "|" + data.getPaymentCategory() + "|" + data.getCurrency() +
//                "|" + data.getPaymentChannel() + "|ADD_PAYMENT_INFO|noonpayments_sdk";
//        sha512 = sha512.toLowerCase();
//        String hash = hashString(sha512);
//        String signature = CommonMethods.INSTANCE.encrypt(hash, data.getPublicKey());
//
//        return signature;
//    }
//
//    private String hashString(String input) {
//        try {
//
//            MessageDigest md = MessageDigest.getInstance("SHA-512");
//            byte[] messageDigest = md.digest(input.getBytes());
//            BigInteger no = new BigInteger(1, messageDigest);
//            String hashtext = no.toString(16);
//            while (hashtext.length() < 128) {
//                hashtext = "0" + hashtext;
//            }
//            return hashtext;
//        } catch (Exception ex) {
//            return "";
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent resultdata) {
//        super.onActivityResult(requestCode, resultCode, resultdata);
//        // check if the request code is same as what is passed  here it is 2
//        if (resultCode == Activity.RESULT_OK && requestCode == 4000) {
//            String rOrderId = resultdata.getStringExtra("orderid");
//            String rMerchantId = resultdata.getStringExtra("merchantid");
//
//            if (rOrderId.isEmpty() || rMerchantId.isEmpty() || !rOrderId.equals(data.getOrderId())) {
//                //payment failure
//                String message = Helper.getLanguageTextByUser(this, "payment_error_reference", userUI.getLanguage(), "");
//                noonPaymentsResponse.setDetails(Helper.STATUS_FAILURE, message, "", responseJson);
//                displayResult(false, context.getResources().getString(R.string.payment_failed),
//                        message);
//            } else {
//                //validate the order by calling inquiry
//                validateTransacton("");
//            }
//        }
//    }
//}