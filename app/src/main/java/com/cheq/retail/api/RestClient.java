package com.cheq.retail.api;


import static com.cheq.retail.constants.AFConstants.FAILED;
import static com.cheq.retail.constants.AFConstants.SUCCESS;
import static com.cheq.retail.constants.Constant.AUTHORIZATION;
import static com.cheq.retail.constants.Constant.BEARER;
import static com.cheq.retail.sharePreferences.SharePrefsKeys.ENCRYPTION_KEY;
import static com.cheq.retail.sharePreferences.SharePrefsKeys.HOST;
import static com.cheq.retail.sharePreferences.SharePrefsKeys.SSL_ENABLED;
import static com.cheq.retail.sharePreferences.SharePrefsKeys.SSL_KEYS;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cheq.retail.BuildConfig;
import com.cheq.retail.api.interceptor.HeaderInterceptor;
import com.cheq.retail.application.MainApplication;
import com.cheq.retail.custom.ShouldNotDecrypt;
import com.cheq.retail.sharePreferences.SharePrefCheqUserId;
import com.cheq.retail.sharePreferences.SharePrefsKeys;
import com.cheq.retail.sharePreferences.SharePrefsReferral;
import com.cheq.retail.ui.splash.model.RefreshToken;
import com.cheq.retail.ui.splash.model.TokenUpdateResponse;
import com.cheq.retail.utils.Aes256;
import com.cheq.retail.utils.EncryptionPass;
import com.cheq.retail.utils.Utils;
import com.chuckerteam.chucker.api.ChuckerInterceptor;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Authenticator;
import okhttp3.CertificatePinner;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Invocation;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by User on 03-11-2018.
 */
public class RestClient {
    private static Retrofit retrofit = null;
    private static RestClient ourInstance;
    String status = "";
    public static RestClient getInstance() {
        if(ourInstance == null) {
            ourInstance = new RestClient();
        }
        return ourInstance;
    }

    public static RestClient getInstance(Context context) {
        if(ourInstance == null) {
            ourInstance = new RestClient(context);
        }
        return ourInstance;
    }

    private RestClient() {
        this(MainApplication.Companion.getApplicationContext());
    }

    private RestClient(Context context) {

        SharePrefsReferral prefs = SharePrefsReferral.Companion.getInstance(context);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.addInterceptor(new ChuckerInterceptor(context));
        if (prefs != null && prefs.getBoolean(SSL_ENABLED)) {
            CertificatePinner certificatePinner = new CertificatePinner.Builder()
                    .add(
                            Objects.requireNonNull(prefs.getString(HOST)),
                            Objects.requireNonNull(prefs.getList(SSL_KEYS)).toArray(new String[0])

                    ).build();
            okBuilder.certificatePinner(certificatePinner);
        }
        OkHttpClient client = okBuilder.readTimeout(2, TimeUnit.MINUTES).connectTimeout(20, TimeUnit.SECONDS).writeTimeout(2, TimeUnit.MINUTES)
                .authenticator(new TokenAuthenticator())
                .addInterceptor(chain -> {
                    Response response = null;
                    try {
                        Request request = chain.request();
                        response = chain.proceed(request);
                        long tx = response.sentRequestAtMillis();
                        long rx = response.receivedResponseAtMillis();


                        boolean shouldNotDecrypt = true;

                        try {
                            shouldNotDecrypt = request
                                    .tag(Invocation.class)
                                    .method()
                                    .getAnnotation(ShouldNotDecrypt.class) != null;
                        } catch (Exception e) {
                            shouldNotDecrypt = false;
                        }

                        if (BuildConfig.SHOULD_DECRYPT_RESPONSE
                                && !shouldNotDecrypt
                        ) {

                            if (response.code() == 201 || response.code() == 200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                                    JSONObject decryptedData = new JSONObject();

                                    if (jsonObject.has("data") && !jsonObject.getString("data").matches("null") && !jsonObject.getString("data").isEmpty()) {

                                        if (!EncryptionPass.INSTANCE.getDecryptedText().isEmpty()) {
                                            String data = Aes256.decrypt(jsonObject.getString("data"),EncryptionPass.INSTANCE.getDecryptedText());

                                            decryptedData = new JSONObject(data);


                                        } else {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("Event", "FIREBASE_PASSWORD_FAILED");
                                            FirebaseAnalytics.getInstance(MainApplication.Companion.getApplicationContext()).logEvent("FIREBASE_PASSWORD_FAILED", bundle);
                                        }
                                    } else {
                                        decryptedData = jsonObject;
                                    }


                                    if (jsonObject.has("message")) {
                                        decryptedData.put("apiMessage", jsonObject.getString("message"));
                                    }
                                    if (jsonObject.has("user_err_msg") && !jsonObject.isNull("user_err_msg") && !jsonObject.getString("user_err_msg").isEmpty()) {
                                        decryptedData.put("user_err_msg", jsonObject.getString("user_err_msg"));
                                    }
                                    if (jsonObject.has("status")) {
                                        decryptedData.put("status", jsonObject.getBoolean("status"));
                                    }

                                    if (BuildConfig.DEBUG) {
                                        Log.d("Decrypted Response :: ", "" + decryptedData);
                                    }

                                    MediaType contentType = Objects.requireNonNull(response.body()).contentType();
                                    ResponseBody responseBody = ResponseBody.create(contentType, decryptedData.toString());

                                    return response.newBuilder().body(responseBody).build();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } finally {
                                    closeConnection(response);
                                }
                            } else if (response.code() == 401 || response.code() == 420) {
                                ErrorManager.Companion.handleServerError(response.code());
                                try {
                                    JSONObject decryptedData = new JSONObject();
                                    decryptedData.put("data", null);
                                    decryptedData.put("apiMessage", null);
                                    if (BuildConfig.DEBUG) {
                                        Log.d("Decrypted Response :: ", "" + decryptedData);
                                    }
                                    MediaType contentType = Objects.requireNonNull(response.body()).contentType();
                                    ResponseBody responseBody = ResponseBody.create(contentType, decryptedData.toString());
                                    return response.newBuilder().body(responseBody).build();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } finally {
                                    closeConnection(response);
                                }
                            } else {
                                try {
                                    JSONObject decryptedData = new JSONObject();
                                    decryptedData.put("data", null);
                                    decryptedData.put("apiMessage", null);

                                    if (BuildConfig.DEBUG) {
                                        Log.d("Decrypted Response :: ", "" + decryptedData);
                                    }

                                    MediaType contentType = Objects.requireNonNull(response.body()).contentType();
                                    ResponseBody responseBody = ResponseBody.create(contentType, decryptedData.toString());

                                    return response.newBuilder().body(responseBody).build();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                } finally {
                                    closeConnection(response);
                                }
                            }
                        } else {

                            try {
                                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                                if (BuildConfig.DEBUG) {
                                    Log.d("Decryptd Response==", "" + jsonObject);
                                }

                                MediaType contentType = Objects.requireNonNull(response.body()).contentType();
                                ResponseBody responseBody = ResponseBody.create(contentType, jsonObject.toString());

                                return response.newBuilder().body(responseBody).build();
                            } catch (Exception e) {
                                e.printStackTrace();
                                FirebaseCrashlytics.getInstance().recordException(e);
                            } finally {
                                closeConnection(response);
                            }

                        }
                    }catch(UnknownHostException e){
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }catch(SocketTimeoutException e){
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                    catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    } finally {
                        closeConnection(response);
                    }

                    return chain.proceed(chain.request());
                }).addInterceptor(chain -> {
                    Request request = chain.request().newBuilder().header(AUTHORIZATION, BEARER + Utils.Companion.getToken(MainApplication.Companion.getApplicationContext())).build();
                    return chain.proceed(request);
                }).addInterceptor(new HeaderInterceptor()).addInterceptor(interceptor)
                .build();


        if (retrofit == null) {
            GsonBuilder b = new GsonBuilder();
            retrofit = new Retrofit.Builder().baseUrl(BuildConfig.apiEndPoint).addConverterFactory(GsonConverterFactory.create(b.create())).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(client).build();
        }


    }

    private void closeConnection(Response response) {
        if (response != null && response.body() != null) {
            response.body().close();
        }
    }

    public APIServices getService() {
        return retrofit.create(APIServices.class);
    }

    public ReferralAPI getReferralApi() {
        return retrofit.create(ReferralAPI.class);
    }

}