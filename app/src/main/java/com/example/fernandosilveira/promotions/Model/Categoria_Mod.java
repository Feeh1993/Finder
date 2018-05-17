package com.example.fernandosilveira.promotions.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fernando Silveira on 29/01/2018.
 */

public class Categoria_Mod
{
    public String cat;
    public int img;

    public Map<String, Boolean> produt = new HashMap<>();
    public Categoria_Mod(String cat, int img)
    {
        this.cat = cat;
        this.img = img;
    }
    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("img",img);
        result.put("cat",cat);
        return result;
    }
}
