package com.facebook.appevents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.Callback;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger.FlushBehavior;
import com.facebook.internal.Logger;
import com.facebook.internal.Utility;
import com.facebook.internal.Utility.FetchedAppSettings;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;

class AppEventQueue {
    private static final String TAG = "com.facebook.appevents.AppEventQueue";
    /* access modifiers changed from: private */
    public static volatile AppEventCollection appEventCollection = new AppEventCollection();
    /* access modifiers changed from: private */
    public static final Runnable flushRunnable = new Runnable() {
        public void run() {
            AppEventQueue.scheduledFuture = null;
            if (AppEventsLogger.getFlushBehavior() != FlushBehavior.EXPLICIT_ONLY) {
                AppEventQueue.flushAndWait(FlushReason.TIMER);
            }
        }
    };
    /* access modifiers changed from: private */
    public static ScheduledFuture scheduledFuture;
    /* access modifiers changed from: private */
    public static final ScheduledExecutorService singleThreadExecutor = Executors.newSingleThreadScheduledExecutor();

    AppEventQueue() {
    }

    public static void persistToDisk() {
        singleThreadExecutor.execute(new Runnable() {
            public void run() {
                AppEventStore.persistEvents(AppEventQueue.appEventCollection);
                AppEventQueue.appEventCollection = new AppEventCollection();
            }
        });
    }

    public static void flush(final FlushReason flushReason) {
        singleThreadExecutor.execute(new Runnable() {
            public void run() {
                AppEventQueue.flushAndWait(flushReason);
            }
        });
    }

    public static void add(final AccessTokenAppIdPair accessTokenAppIdPair, final AppEvent appEvent) {
        singleThreadExecutor.execute(new Runnable() {
            public void run() {
                AppEventQueue.appEventCollection.addEvent(accessTokenAppIdPair, appEvent);
                if (AppEventsLogger.getFlushBehavior() != FlushBehavior.EXPLICIT_ONLY && AppEventQueue.appEventCollection.getEventCount() > 100) {
                    AppEventQueue.flushAndWait(FlushReason.EVENT_THRESHOLD);
                } else if (AppEventQueue.scheduledFuture == null) {
                    AppEventQueue.scheduledFuture = AppEventQueue.singleThreadExecutor.schedule(AppEventQueue.flushRunnable, 15, TimeUnit.SECONDS);
                }
            }
        });
    }

    public static Set<AccessTokenAppIdPair> getKeySet() {
        return appEventCollection.keySet();
    }

    static void flushAndWait(FlushReason flushReason) {
        appEventCollection.addPersistedEvents(AppEventStore.readAndClearStore());
        try {
            FlushStatistics sendEventsToServer = sendEventsToServer(flushReason, appEventCollection);
            if (sendEventsToServer != null) {
                Intent intent = new Intent("com.facebook.sdk.APP_EVENTS_FLUSHED");
                intent.putExtra("com.facebook.sdk.APP_EVENTS_NUM_EVENTS_FLUSHED", sendEventsToServer.numEvents);
                intent.putExtra("com.facebook.sdk.APP_EVENTS_FLUSH_RESULT", sendEventsToServer.result);
                LocalBroadcastManager.getInstance(FacebookSdk.getApplicationContext()).sendBroadcast(intent);
            }
        } catch (Exception e) {
            Log.w(TAG, "Caught unexpected exception while flushing app events: ", e);
        }
    }

    private static FlushStatistics sendEventsToServer(FlushReason flushReason, AppEventCollection appEventCollection2) {
        FlushStatistics flushStatistics = new FlushStatistics();
        boolean limitEventAndDataUsage = FacebookSdk.getLimitEventAndDataUsage(FacebookSdk.getApplicationContext());
        ArrayList<GraphRequest> arrayList = new ArrayList<>();
        for (AccessTokenAppIdPair accessTokenAppIdPair : appEventCollection2.keySet()) {
            GraphRequest buildRequestForSession = buildRequestForSession(accessTokenAppIdPair, appEventCollection2.get(accessTokenAppIdPair), limitEventAndDataUsage, flushStatistics);
            if (buildRequestForSession != null) {
                arrayList.add(buildRequestForSession);
            }
        }
        if (arrayList.size() <= 0) {
            return null;
        }
        Logger.log(LoggingBehavior.APP_EVENTS, TAG, "Flushing %d events due to %s.", Integer.valueOf(flushStatistics.numEvents), flushReason.toString());
        for (GraphRequest executeAndWait : arrayList) {
            executeAndWait.executeAndWait();
        }
        return flushStatistics;
    }

    private static GraphRequest buildRequestForSession(final AccessTokenAppIdPair accessTokenAppIdPair, final SessionEventsState sessionEventsState, boolean z, final FlushStatistics flushStatistics) {
        String applicationId = accessTokenAppIdPair.getApplicationId();
        FetchedAppSettings queryAppSettings = Utility.queryAppSettings(applicationId, false);
        final GraphRequest newPostRequest = GraphRequest.newPostRequest(null, String.format("%s/activities", new Object[]{applicationId}), null, null);
        Bundle parameters = newPostRequest.getParameters();
        if (parameters == null) {
            parameters = new Bundle();
        }
        parameters.putString("access_token", accessTokenAppIdPair.getAccessTokenString());
        String pushNotificationsRegistrationId = AppEventsLogger.getPushNotificationsRegistrationId();
        if (pushNotificationsRegistrationId != null) {
            parameters.putString("device_token", pushNotificationsRegistrationId);
        }
        newPostRequest.setParameters(parameters);
        if (queryAppSettings == null) {
            return null;
        }
        int populateRequest = sessionEventsState.populateRequest(newPostRequest, FacebookSdk.getApplicationContext(), queryAppSettings.supportsImplicitLogging(), z);
        if (populateRequest == 0) {
            return null;
        }
        flushStatistics.numEvents += populateRequest;
        newPostRequest.setCallback(new Callback() {
            public void onCompleted(GraphResponse graphResponse) {
                AppEventQueue.handleResponse(accessTokenAppIdPair, newPostRequest, graphResponse, sessionEventsState, flushStatistics);
            }
        });
        return newPostRequest;
    }

    /* access modifiers changed from: private */
    public static void handleResponse(final AccessTokenAppIdPair accessTokenAppIdPair, GraphRequest graphRequest, GraphResponse graphResponse, final SessionEventsState sessionEventsState, FlushStatistics flushStatistics) {
        String str;
        FacebookRequestError error = graphResponse.getError();
        String str2 = "Success";
        FlushResult flushResult = FlushResult.SUCCESS;
        boolean z = true;
        if (error != null) {
            if (error.getErrorCode() == -1) {
                str2 = "Failed: No Connectivity";
                flushResult = FlushResult.NO_CONNECTIVITY;
            } else {
                str2 = String.format("Failed:\n  Response: %s\n  Error %s", new Object[]{graphResponse.toString(), error.toString()});
                flushResult = FlushResult.SERVER_ERROR;
            }
        }
        if (FacebookSdk.isLoggingBehaviorEnabled(LoggingBehavior.APP_EVENTS)) {
            try {
                str = new JSONArray((String) graphRequest.getTag()).toString(2);
            } catch (JSONException unused) {
                str = "<Can't encode events for debug logging>";
            }
            Logger.log(LoggingBehavior.APP_EVENTS, TAG, "Flush completed\nParams: %s\n  Result: %s\n  Events JSON: %s", graphRequest.getGraphObject().toString(), str2, str);
        }
        if (error == null) {
            z = false;
        }
        sessionEventsState.clearInFlightAndStats(z);
        if (flushResult == FlushResult.NO_CONNECTIVITY) {
            FacebookSdk.getExecutor().execute(new Runnable() {
                public void run() {
                    AppEventStore.persistEvents(accessTokenAppIdPair, sessionEventsState);
                }
            });
        }
        if (flushResult != FlushResult.SUCCESS && flushStatistics.result != FlushResult.NO_CONNECTIVITY) {
            flushStatistics.result = flushResult;
        }
    }
}
