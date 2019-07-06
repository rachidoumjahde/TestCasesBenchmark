package io.branch.indexing;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.google.firebase.appindexing.a.C0150a;
import com.google.firebase.appindexing.a.b.C0151a;
import com.google.firebase.appindexing.b;
import com.google.firebase.appindexing.c;
import com.google.firebase.appindexing.d;
import io.branch.referral.m;
import io.branch.referral.util.LinkProperties;
import java.lang.reflect.Method;

/* compiled from: AppIndexingHelper */
class a {
    /* access modifiers changed from: private */
    public static c a;
    /* access modifiers changed from: private */
    public static final LinkProperties b = new LinkProperties().setChannel("google_search");

    static void a(final Context context, final BranchUniversalObject branchUniversalObject, final LinkProperties linkProperties) {
        new Thread(new Runnable() {
            public void run() {
                String str;
                try {
                    a.a = c.a();
                } catch (NoClassDefFoundError unused) {
                    m.b("BranchSDK", "Firebase app indexing is not available. Please consider enabling Firebase app indexing for your app for better indexing experience with Google.");
                } catch (Throwable unused2) {
                    m.b("BranchSDK", "Failed to index your contents using Firebase. Please make sure Firebase  is enabled and initialised in your app");
                }
                if (linkProperties == null) {
                    str = branchUniversalObject.getShortUrl(context, a.b);
                } else {
                    str = branchUniversalObject.getShortUrl(context, linkProperties);
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Indexing BranchUniversalObject with Google using URL ");
                sb.append(str);
                m.b("BranchSDK", sb.toString());
                if (!TextUtils.isEmpty(str)) {
                    try {
                        if (a.a != null) {
                            a.b(str, branchUniversalObject);
                        } else {
                            a.b(str, context, branchUniversalObject);
                        }
                    } catch (Throwable unused3) {
                        m.b("BranchSDK", "Branch Warning: Unable to list your content in Google search. Please make sure you have added latest Firebase app indexing SDK to your project dependencies.");
                    }
                }
            }
        }).start();
    }

    static void b(final Context context, final BranchUniversalObject branchUniversalObject, final LinkProperties linkProperties) {
        new Thread(new Runnable() {
            public void run() {
                String str;
                try {
                    if (linkProperties == null) {
                        str = branchUniversalObject.getShortUrl(context, a.b);
                    } else {
                        str = branchUniversalObject.getShortUrl(context, linkProperties);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Removing indexed BranchUniversalObject with link ");
                    sb.append(str);
                    m.b("BranchSDK", sb.toString());
                    b.a().a(str);
                } catch (NoClassDefFoundError unused) {
                    m.b("BranchSDK", "Failed to remove the BranchUniversalObject from Firebase local indexing. Please make sure Firebase is enabled and initialised in your app");
                } catch (Throwable unused2) {
                    m.b("BranchSDK", "Failed to index your contents using Firebase. Please make sure Firebase is enabled and initialised in your app");
                }
            }
        }).run();
    }

    /* access modifiers changed from: private */
    public static void b(String str, BranchUniversalObject branchUniversalObject) {
        StringBuilder sb = new StringBuilder();
        sb.append(branchUniversalObject.getTitle());
        sb.append("\n");
        sb.append(branchUniversalObject.getDescription());
        String sb2 = sb.toString();
        if (branchUniversalObject.isLocallyIndexable()) {
            d a2 = com.google.firebase.appindexing.a.b.a(sb2, str);
            b.a().a(a2);
        }
        a.a(new C0150a("ViewAction").a(sb2, str).a(new C0151a().a(branchUniversalObject.isPublicallyIndexable())).a());
    }

    /* access modifiers changed from: private */
    public static void b(String str, Context context, BranchUniversalObject branchUniversalObject) throws Exception {
        Class cls = Class.forName("com.google.android.gms.a.e");
        Class cls2 = Class.forName("com.google.android.gms.a.e$a");
        Object newInstance = cls2.getConstructor(new Class[0]).newInstance(new Object[0]);
        Method method = cls2.getMethod("setName", new Class[]{String.class});
        Method method2 = cls2.getMethod("setDescription", new Class[]{String.class});
        Method method3 = cls2.getMethod("setUrl", new Class[]{Uri.class});
        Method method4 = cls2.getMethod("build", new Class[0]);
        method.invoke(newInstance, new Object[]{branchUniversalObject.getTitle()});
        method2.invoke(newInstance, new Object[]{branchUniversalObject.getDescription()});
        method3.invoke(newInstance, new Object[]{Uri.parse(str)});
        Object invoke = method4.invoke(newInstance, new Object[0]);
        Class cls3 = Class.forName("com.google.android.gms.a.a");
        Class cls4 = Class.forName("com.google.android.gms.a.a$a");
        Object newInstance2 = cls4.getConstructor(new Class[]{String.class}).newInstance(new Object[]{(String) cls3.getDeclaredField("TYPE_VIEW").get(null)});
        Method method5 = cls4.getMethod("setObject", new Class[]{cls});
        Method method6 = cls4.getMethod("setActionStatus", new Class[]{String.class});
        Method method7 = cls4.getMethod("build", new Class[0]);
        method5.invoke(newInstance2, new Object[]{invoke});
        method6.invoke(newInstance2, new Object[]{(String) cls3.getDeclaredField("STATUS_TYPE_COMPLETED").get(null)});
        Object invoke2 = method7.invoke(newInstance2, new Object[0]);
        Class cls5 = Class.forName("com.google.android.gms.a.c");
        Class cls6 = Class.forName("com.google.android.gms.common.api.Api");
        Class cls7 = Class.forName("com.google.android.gms.common.api.GoogleApiClient");
        Class cls8 = Class.forName("com.google.android.gms.common.api.GoogleApiClient$Builder");
        Object newInstance3 = cls8.getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
        Method method8 = cls8.getMethod("addApi", new Class[]{cls6});
        Method method9 = cls8.getMethod("build", new Class[0]);
        Method method10 = cls7.getMethod("connect", new Class[0]);
        Method method11 = cls7.getMethod("disconnect", new Class[0]);
        method8.invoke(newInstance3, new Object[]{cls6.cast(cls5.getDeclaredField("API").get(null))});
        Object invoke3 = method9.invoke(newInstance3, new Object[0]);
        method10.invoke(invoke3, new Object[0]);
        Class cls9 = Class.forName("com.google.android.gms.a.d");
        Object obj = cls5.getDeclaredField("AppIndexApi").get(null);
        cls9.getMethod("start", new Class[]{cls7, cls3}).invoke(obj, new Object[]{invoke3, invoke2});
        method11.invoke(invoke3, new Object[0]);
    }
}