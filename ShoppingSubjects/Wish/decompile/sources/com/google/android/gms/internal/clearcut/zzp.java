package com.google.android.gms.internal.clearcut;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.clearcut.ClearcutLogger.zza;
import com.google.android.gms.clearcut.zze;
import com.google.android.gms.common.wrappers.Wrappers;
import com.google.android.gms.internal.clearcut.zzgw.zza.zzb;
import com.google.android.gms.phenotype.Phenotype;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class zzp implements zza {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final zzao zzaq = new zzao(Phenotype.getContentProviderUri("com.google.android.gms.clearcut.public")).zzc("gms:playlog:service:samplingrules_").zzd("LogSamplingRules__");
    private static final zzao zzar = new zzao(Phenotype.getContentProviderUri("com.google.android.gms.clearcut.public")).zzc("gms:playlog:service:sampling_").zzd("LogSampling__");
    private static final ConcurrentHashMap<String, zzae<zzgw.zza>> zzas = new ConcurrentHashMap<>();
    private static final HashMap<String, zzae<String>> zzat = new HashMap<>();
    private static Boolean zzau;
    private static Long zzav;
    private static final zzae<Boolean> zzaw = zzaq.zzc("enable_log_sampling_rules", false);
    private final Context zzh;

    public zzp(Context context) {
        this.zzh = context;
        if (this.zzh != null) {
            zzae.maybeInit(this.zzh);
        }
    }

    private static long zza(String str, long j) {
        byte[] array;
        if (str == null || str.isEmpty()) {
            array = ByteBuffer.allocate(8).putLong(j).array();
        } else {
            byte[] bytes = str.getBytes(UTF_8);
            ByteBuffer allocate = ByteBuffer.allocate(bytes.length + 8);
            allocate.put(bytes);
            allocate.putLong(j);
            array = allocate.array();
        }
        return zzk.zza(array);
    }

    private static zzb zza(String str) {
        if (str == null) {
            return null;
        }
        String str2 = "";
        int indexOf = str.indexOf(44);
        int i = 0;
        if (indexOf >= 0) {
            str2 = str.substring(0, indexOf);
            i = indexOf + 1;
        }
        int indexOf2 = str.indexOf(47, i);
        if (indexOf2 <= 0) {
            String str3 = "LogSamplerImpl";
            String str4 = "Failed to parse the rule: ";
            String valueOf = String.valueOf(str);
            Log.e(str3, valueOf.length() != 0 ? str4.concat(valueOf) : new String(str4));
            return null;
        }
        try {
            long parseLong = Long.parseLong(str.substring(i, indexOf2));
            long parseLong2 = Long.parseLong(str.substring(indexOf2 + 1));
            if (parseLong >= 0 && parseLong2 >= 0) {
                return (zzb) zzb.zzfz().zzn(str2).zzr(parseLong).zzs(parseLong2).zzbh();
            }
            StringBuilder sb = new StringBuilder(72);
            sb.append("negative values not supported: ");
            sb.append(parseLong);
            sb.append("/");
            sb.append(parseLong2);
            Log.e("LogSamplerImpl", sb.toString());
            return null;
        } catch (NumberFormatException e) {
            String str5 = "LogSamplerImpl";
            String str6 = "parseLong() failed while parsing: ";
            String valueOf2 = String.valueOf(str);
            Log.e(str5, valueOf2.length() != 0 ? str6.concat(valueOf2) : new String(str6), e);
            return null;
        }
    }

    private static boolean zzb(long j, long j2, long j3) {
        if (j2 >= 0 && j3 > 0) {
            if (j < 0) {
                j = (Long.MAX_VALUE % j3) + 1 + ((j & Long.MAX_VALUE) % j3);
            }
            if (j % j3 >= j2) {
                return false;
            }
        }
        return true;
    }

    private static boolean zzc(Context context) {
        if (zzau == null) {
            zzau = Boolean.valueOf(Wrappers.packageManager(context).checkCallingOrSelfPermission("com.google.android.providers.gsf.permission.READ_GSERVICES") == 0);
        }
        return zzau.booleanValue();
    }

    private static long zzd(Context context) {
        if (zzav == null) {
            long j = 0;
            if (context == null) {
                return 0;
            }
            if (zzc(context)) {
                j = zzy.getLong(context.getContentResolver(), "android_id", 0);
            }
            zzav = Long.valueOf(j);
        }
        return zzav.longValue();
    }

    public final boolean zza(zze zze) {
        List<zzb> list;
        String str = zze.zzag.zzj;
        int i = zze.zzag.zzk;
        int i2 = zze.zzaa != null ? zze.zzaa.zzbji : 0;
        String str2 = null;
        if (!((Boolean) zzaw.get()).booleanValue()) {
            if (str == null || str.isEmpty()) {
                str = i >= 0 ? String.valueOf(i) : null;
            }
            if (str != null) {
                if (this.zzh != null && zzc(this.zzh)) {
                    zzae zzae = (zzae) zzat.get(str);
                    if (zzae == null) {
                        zzae = zzar.zza(str, null);
                        zzat.put(str, zzae);
                    }
                    str2 = (String) zzae.get();
                }
                zzb zza = zza(str2);
                if (zza != null) {
                    return zzb(zza(zza.zzfw(), zzd(this.zzh)), zza.zzfx(), zza.zzfy());
                }
            }
        } else {
            if (str == null || str.isEmpty()) {
                str = i >= 0 ? String.valueOf(i) : null;
            }
            if (str != null) {
                if (this.zzh == null) {
                    list = Collections.emptyList();
                } else {
                    zzae zzae2 = (zzae) zzas.get(str);
                    if (zzae2 == null) {
                        zzae2 = zzaq.zza(str, zzgw.zza.zzft(), zzq.zzax);
                        zzae zzae3 = (zzae) zzas.putIfAbsent(str, zzae2);
                        if (zzae3 != null) {
                            zzae2 = zzae3;
                        }
                    }
                    list = ((zzgw.zza) zzae2.get()).zzfs();
                }
                for (zzb zzb : list) {
                    if ((!zzb.zzfv() || zzb.getEventCode() == 0 || zzb.getEventCode() == i2) && !zzb(zza(zzb.zzfw(), zzd(this.zzh)), zzb.zzfx(), zzb.zzfy())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
