package com.example.fernandosilveira.promotions.Validacao;

import java.util.Set;

/**
 * Created by Fernando Silveira on 05/09/2017.
 */

public abstract class Constants
{
        public static String tirarmasc(String s, Set<String> replaceSymbols)
        {
            for (String symbol : replaceSymbols)
                s = s.replaceAll("["+symbol+"]","");
            return s;
        }
        public static String masc(String formato, String texto)
        {
            String mascText="";
            int i =0;
            for (char m : formato.toCharArray())
            {
                if (m != '#')
                {
                    mascText += m;
                    continue;
                }
                try
                {
                    mascText += texto.charAt(i);
                } catch (Exception e)
                {
                    break;
                }
                i++;
            }
            return mascText;
        }
}