package com.example.fernandosilveira.promotions.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fernando Silveira on 21/07/2017.
 */

public class LocalProd
{
    public Double lat;
    public Double lng;
    public String nomeProd;
    public String preco;
    public String diasRestantes;
    public String categoria;
    public String tipo;
    public String uid;
    public String empres;
    public Map<String, Boolean> locprod = new HashMap<>();

    public LocalProd() {
    }

    public LocalProd(Double lat, Double lng, String nomeProd, String preco, String diasRestantes, String categoria, String uid, String empres,String tipo) {
        this.lat = lat;
        this.lng = lng;
        this.nomeProd = nomeProd;
        this.preco = preco;
        this.diasRestantes = diasRestantes;
        this.categoria = categoria;
        this.uid = uid;
        this.tipo =tipo;
        this.empres = empres;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("lat", lat);
        result.put("lng", lng);
        result.put("nomeProd", nomeProd);
        result.put("preco", preco);
        result.put("diasRestantes", diasRestantes);
        result.put("categoria", categoria);
        result.put("uid", uid);
        result.put("empres",empres);
        result.put("tipo",tipo);

        return result;
    }
}
