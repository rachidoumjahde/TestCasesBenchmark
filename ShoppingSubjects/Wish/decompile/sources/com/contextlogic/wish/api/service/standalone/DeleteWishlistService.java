package com.contextlogic.wish.api.service.standalone;

import com.contextlogic.wish.api.ApiRequest;
import com.contextlogic.wish.api.ApiResponse;
import com.contextlogic.wish.api.service.ApiService.DefaultFailureCallback;
import com.contextlogic.wish.api.service.ApiService.DefaultSuccessCallback;
import com.contextlogic.wish.api.service.SingleApiService;

public class DeleteWishlistService extends SingleApiService {
    public void requestService(String str, final DefaultSuccessCallback defaultSuccessCallback, final DefaultFailureCallback defaultFailureCallback) {
        ApiRequest apiRequest = new ApiRequest("user/wishlist/delete");
        apiRequest.addParameter("wishlist_id", (Object) str);
        startService(apiRequest, new ApiCallback() {
            public String getCallIdentifier() {
                return null;
            }

            public void handleFailure(ApiResponse apiResponse, final String str) {
                if (defaultFailureCallback != null) {
                    DeleteWishlistService.this.postRunnable(new Runnable() {
                        public void run() {
                            defaultFailureCallback.onFailure(str);
                        }
                    });
                }
            }

            public void handleSuccess(ApiResponse apiResponse) {
                if (defaultSuccessCallback != null) {
                    DeleteWishlistService.this.postRunnable(new Runnable() {
                        public void run() {
                            defaultSuccessCallback.onSuccess();
                        }
                    });
                }
            }
        });
    }
}
