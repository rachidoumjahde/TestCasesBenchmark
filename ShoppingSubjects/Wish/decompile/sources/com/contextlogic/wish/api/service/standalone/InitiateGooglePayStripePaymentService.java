package com.contextlogic.wish.api.service.standalone;

import com.contextlogic.wish.api.ApiRequest;
import com.contextlogic.wish.api.ApiResponse;
import com.contextlogic.wish.api.service.SingleApiService;
import com.google.android.gms.wallet.PaymentData;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONException;

public class InitiateGooglePayStripePaymentService extends SingleApiService {

    public interface FailureCallback {
        void onFailure(String str, int i);
    }

    public interface SuccessCallback {
        void onSuccess(String str);
    }

    public void requestService(String str, String str2, PaymentData paymentData, String str3, int i, SuccessCallback successCallback, FailureCallback failureCallback) {
        ApiRequest apiRequest = getApiRequest(str, str2, paymentData.getCardInfo().getBillingAddress().getPostalCode(), paymentData.getEmail(), new ArrayList(Arrays.asList(new String[]{paymentData.getCardInfo().getCardDescription()})), str3, i);
        final SuccessCallback successCallback2 = successCallback;
        final FailureCallback failureCallback2 = failureCallback;
        startService(apiRequest, new ApiCallback() {
            public String getCallIdentifier() {
                return null;
            }

            public void handleFailure(final ApiResponse apiResponse, final String str) {
                if (failureCallback2 != null) {
                    InitiateGooglePayStripePaymentService.this.postRunnable(new Runnable() {
                        public void run() {
                            failureCallback2.onFailure(str, apiResponse != null ? apiResponse.getCode() : -1);
                        }
                    });
                }
            }

            public void handleSuccess(ApiResponse apiResponse) throws JSONException {
                final String string = apiResponse.getData().getString("transaction_id");
                if (successCallback2 != null) {
                    InitiateGooglePayStripePaymentService.this.postRunnable(new Runnable() {
                        public void run() {
                            successCallback2.onSuccess(string);
                        }
                    });
                }
            }
        });
    }

    private ApiRequest getApiRequest(String str, String str2, String str3, String str4, ArrayList<String> arrayList, String str5, int i) {
        ApiRequest apiRequest = new ApiRequest("payment/google-wallet/stripe/complete");
        apiRequest.addParameter("stripe_token", (Object) str2);
        apiRequest.addParameter("cart_type", i);
        if (str5 != null) {
            apiRequest.addParameter("checkout_offer_id", (Object) str5);
        }
        if (str4 != null) {
            apiRequest.addParameter("google_wallet_email", (Object) str4);
        }
        if (arrayList != null && arrayList.size() > 0) {
            apiRequest.addParameter("google_wallet_funding_sources[]", arrayList);
        }
        apiRequest.addParameter("billing_zipcode", (Object) str3);
        apiRequest.addParameter("currency", (Object) str);
        return apiRequest;
    }
}