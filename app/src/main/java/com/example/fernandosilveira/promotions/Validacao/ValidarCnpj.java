package com.example.fernandosilveira.promotions.Validacao;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Fernando Silveira on 29/06/2017.
 */
public abstract class ValidarCnpj
{
       private static final String mascCNPJ = "##.###.###/####-##";
       private static final String mascCPF = "###.###.###-##";
        public static String unmask(String s) {
            return s.replaceAll("[^0-9]*", "");
        }
        public static TextWatcher inserir(final EditText editText)
        {
            return new TextWatcher()
            {
                boolean isUpdating;
                String old = "";
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    String str = ValidarCnpj.unmask(s.toString());
                    String mask;
                    String defaultMask = getDefaultMask(str);
                    switch (str.length())
                    {
                        case 11:
                            mask = mascCPF;
                            break;
                        case 14:
                            mask = mascCNPJ;
                            break;
                        default:
                            mask = defaultMask;
                            break;
                    }
                    String mascara = "";
                    if (isUpdating)
                    {
                        old = str;
                        isUpdating = false;
                        return;
                    }
                    int i = 0;
                    for (char m : mask.toCharArray())
                    {
                        if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i))
                        {
                            mascara += m;
                            continue;
                        }
                        try
                        {
                            mascara += str.charAt(i);
                        } catch (Exception e)
                        {
                            break;
                        }
                        i++;
                    }
                    isUpdating = true;
                    editText.setText(mascara);
                    editText.setSelection(mascara.length());
                }
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after)
                {

                }
                public void afterTextChanged(Editable s) {
                }
            };
        }
        private static String getDefaultMask(String str)
        {
            String defaultMask = mascCPF;
            if (str.length() > 11)
            {
                defaultMask = mascCNPJ;
            }
            return defaultMask;
        }
    }