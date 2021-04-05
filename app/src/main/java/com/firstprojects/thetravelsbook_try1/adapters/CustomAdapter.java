package com.firstprojects.thetravelsbook_try1.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firstprojects.thetravelsbook_try1.R;
import com.firstprojects.thetravelsbook_try1.models.AddressRegister;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter<stringArrayList> extends ArrayAdapter<AddressRegister> {
    private Activity context;
    private ArrayList<AddressRegister> addressRegister;

    public CustomAdapter(Activity context,ArrayList<AddressRegister> addressRegister) {
        super(context, R.layout.custom_adapter_layout, addressRegister);
        this.context = context;
        this.addressRegister = addressRegister;
    }

    @NonNull
    @Override
    public Activity getContext() {
        return context;
    }

    public ArrayList<AddressRegister> getAddressRegisters() {
        return addressRegister;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customAdapter = layoutInflater.inflate(R.layout.custom_adapter_layout,null,true);
        TextView adapterName = customAdapter.findViewById(R.id.customAdapterItem);
        adapterName.setText(addressRegister.get(position).getPlaceName());

        return customAdapter;

    }
}
