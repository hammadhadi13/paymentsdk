//package com.noonpayments.paymentsdk.activities;
//
//import static com.noonpayments.paymentsdk.models.NoonPaymentsAPIConfig.NOON_URL_LIVE_ORDER;
//import static com.noonpayments.paymentsdk.models.NoonPaymentsAPIConfig.NOON_URL_TEST_ORDER;
//
//import android.content.Context;
//import android.content.res.Configuration;
//import android.content.res.Resources;
//import android.os.Bundle;
//import android.os.Handler;
//import android.widget.Toast;
//
//import androidx.annotation.OptIn;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.Lifecycle;
//
//import com.arhamsoft.ilets.domain.repositories.GetApiResponseRepo;
//import com.noonpayments.paymentsdk.Utils.CommonMethods;
//import com.noonpayments.paymentsdk.Utils.URLs;
//import com.noonpayments.paymentsdk.ViewModel.ApiCallingViewModel;
//import com.noonpayments.paymentsdk.helpers.Helper;
//import com.noonpayments.paymentsdk.models.NoonPaymentsData;
//import com.noonpayments.paymentsdk.models.NoonPaymentsResponse;
//import com.noonpayments.paymentsdk.models.NoonPaymentsSetup;
//import com.noonpayments.paymentsdk.models.NoonPaymentsUI;
//import com.noonpayments.paymentsdk.models.PaymentMode;
//import com.retech.yapiee.domain.retrofit.RetrofitClient;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.Locale;
//import java.util.Objects;
//
//import kotlinx.coroutines.BuildersKt;
//import kotlinx.coroutines.CoroutineStart;
//import kotlinx.coroutines.Dispatchers;
//import kotlinx.coroutines.GlobalScope;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class BaseActivity extends AppCompatActivity {
//
//    static String language = "en";
//    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//    Handler mainHandler;
//    Helper helper;
//    NoonPaymentsSetup setup;
//    NoonPaymentsUI userUI;
//    NoonPaymentsData data;
//    NoonPaymentsResponse noonPaymentsResponse;
//    private ApiCallingViewModel apiCallingViewModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mainHandler = new Handler(this.getMainLooper());
//        helper = new Helper();
//
//        //setup the UI
//        apiCallingViewModel = new ApiCallingViewModel(new GetApiResponseRepo(RetrofitClient.Companion.getInstance()));
//        setup = NoonPaymentsSetup.getInstance();
//        userUI = setup.getNoonUI();
//        data = setup.getNoonData();
//        noonPaymentsResponse = new NoonPaymentsResponse();
//
//    }
//
//    public Context setLocale(Context activity, String languageCode) {
//        Locale locale = new Locale(languageCode);
//        Locale.setDefault(locale);
//        Resources resources = activity.getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
//
//        return activity;
//    }
//
//    public void callCancelAPI(String orderNumber) {
//
//        try {
//            String url = NOON_URL_LIVE_ORDER;
//            if (data.getPaymentMode() == PaymentMode.TEST)
//                url = NOON_URL_TEST_ORDER;
//
//            URLs.INSTANCE.setFinalBaseUrl(url);
//
//            String json = CommonMethods.INSTANCE.cancelledOrderJSON(orderNumber);
//
//
//            BuildersKt.launch(GlobalScope.INSTANCE,
//                    Dispatchers.getMain(),//context to be ran on
//                    CoroutineStart.DEFAULT,
//                    (coroutineScope, continuation) -> apiCallingViewModel.callCancelApi(json)
//            );
//
////            this.post(url, json);
//        } catch (Exception ex) {
////            String s = ex.getMessage();
////            String ss = Arrays.toString(ex.getStackTrace());
//        }
//    }
//
//    public void post(String url, String json) throws IOException {
//
//        RequestBody body = RequestBody.create(json, JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("Authorization", data.getAuthorizationHeader())
//                .post(body)
//                .build();
//
//        URLs.INSTANCE.getBaseClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                // Something went wrong
//                mainHandler.post(() -> {
//                    Toast toast = Toast.makeText(BaseActivity.this, e.getMessage(), Toast.LENGTH_LONG);
//                    toast.show();
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseStr = Objects.requireNonNull(response.body()).string();
//
//                    try {
//                        final String message;
//                        String resultCode = "";
//                        JSONObject jsonObject = new JSONObject(responseStr);
//                        if (jsonObject.has("resultCode"))
//                            resultCode = jsonObject.getString("resultCode");
////                        if (jsonObject.has("message"))
//                        message = jsonObject.getString("message");
////                        else
////                            message = "Payment initiation failed";
//                        if (resultCode.equals("0")) {
//                            mainHandler.post(() -> {
//                                Toast.makeText(BaseActivity.this, "Payment Cancelled!", Toast.LENGTH_LONG).show();
//                            });
//                        } else {
//                            mainHandler.post(() -> {
//                                Toast toast = Toast.makeText(BaseActivity.this, message, Toast.LENGTH_LONG);
//                                toast.show();
//                            });
//                        }
//                    } catch (Exception ex) {
//                        mainHandler.post(() -> {
//                            Toast toast = Toast.makeText(BaseActivity.this, ex.getMessage(), Toast.LENGTH_LONG);
////                            toast.show();
//                        });
//                    }
//                } else {
//                    // Request not successful
//                    mainHandler.post(() -> {
//                        Toast toast = Toast.makeText(BaseActivity.this, response.message(), Toast.LENGTH_LONG);
////                        toast.show();
//                    });
//                }
//            }
//        });
//    }
//}