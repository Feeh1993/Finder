package com.example.fernandosilveira.promotions.Validacao;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;

/**
 * Created by Fernando Silveira on 13/09/2017.
 */
public class ValidadePreco implements TextWatcher
{
        private String current = "";
        private final EditText currency;
        public ValidadePreco(EditText p_currency) {
            currency = p_currency;
        }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
    }
    @Override
    public void afterTextChanged(Editable s)
    {
        if (!s.toString().equals(current))
        {
            currency.removeTextChangedListener(this);
            String replaceable = String.format("[%s,.\\s]",   NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
            String cleanString = s.toString().replaceAll(replaceable, "");
            double parsed;
            try
            {
                parsed = Double.parseDouble(cleanString);
            } catch (NumberFormatException e)
            {
                parsed = 0.00;
            }
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            formatter.setMaximumFractionDigits(0);
            String formatted = formatter.format((parsed));
            current = formatted;
            currency.setText(formatted);
            currency.setSelection(formatted.length());
            currency.addTextChangedListener(this);
        }
    }
}