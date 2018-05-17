package com.example.fernandosilveira.promotions.Service.Geofence;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencConfig
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // Modo pref compartilhado
    int PRIVATE_MODE = 0;
    // Nome do arquivo Sharedpref
    private static final String PREF_NAME = "club";
    public PreferencConfig(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void putString(String chave, String valor)
    {
        editor.putString(chave, valor);
        editor.apply();
    }
    public String getString(String chave, String valor) {
        return pref.getString(chave, valor);
    }

    public void putBoolean(String chave, boolean valor)
    {
        editor.putBoolean(chave, valor);
        editor.apply();
    }
    public boolean getBoolean(String chave, boolean valor)
    {
        return pref.getBoolean(chave, valor);
    }
    public void putInt(String chave, int valor)
    {
        editor.putInt(chave, valor);
        editor.apply();
    }
    public int getInt(String chave, int valor)
    {
        return pref.getInt(chave, valor);
    }
    public void clearKeyPreference(String chave)
    {
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(chave);
        editor.apply();
    }
}