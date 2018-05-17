package com.example.fernandosilveira.promotions.Config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;


/**
 * Created by Fernando Silveira on 11/08/2017.
 */

public class ConexaoTeste
{
    private boolean statusconexao;
    public String checkNetworkType(Context context)
    {
        boolean b;
        String erro = "";
        b = isConnectedFast(context);
        if(b ==true)
        {
            // conexao esta boa
            erro = "OK";

        } else
            {
                // conexao esta ruim
                 erro = "Sem conexão";
            }
            return erro;
    }
    private boolean isConnectedFast(Context context)
    {
            NetworkInfo info = getNetworkInfo(context);
            return (info != null && info.isConnected() && isConnectionFast(info.getType(),info.getSubtype(), context));
    }
        private boolean isConnectionFast(int tipo, int subTipo, Context context)
        {
            if(tipo == ConnectivityManager.TYPE_WIFI)
            {
                return true;
            }
            else if(tipo == ConnectivityManager.TYPE_MOBILE)
            {
                switch(subTipo)
                {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        return false; // ~ 14-64 kbps
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return false; // ~ 50-100 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return true; // ~ 400-1000 kbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return true; // ~ 600-1400 kbps
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return false; // ~ 100 kbps
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return true; // ~ 2-14 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        return true; // ~ 700-1700 kbps
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return true; // ~ 1-23 Mbps
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        return true; // ~ 400-7000 kbps
// Acima do nível de API 7, certifique-se de definir o android: targetSdkVersion para o nível apropriado para usar estes
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                        return true; // ~ 1-2 Mbps
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                        return true; // ~ 5 Mbps
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                        return true; // ~ 10-20 Mbps
                    case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                        return false; // ~25 kbps
                    case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                        return true; // ~ 10+ Mbps
                    // Unknown
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    default:
                        return false;
                }
            }
            else
                {
                    return false;
                }
        }
        private NetworkInfo getNetworkInfo(Context context)
        {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        }
}