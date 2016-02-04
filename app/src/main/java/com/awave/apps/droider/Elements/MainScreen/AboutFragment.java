package com.awave.apps.droider.Elements.MainScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.awave.apps.droider.Main.AdapterMain;
import com.awave.apps.droider.R;

public class AboutFragment extends android.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.about_fragment, container, false);

        ImageView ArtIm = (ImageView)v.findViewById(R.id.ArtIm);

        ArtIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/awawave")));
            }
        });

        ImageView AlexIm = (ImageView)v.findViewById(R.id.AlexIm);
        AlexIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/jackson23")));
            }
        });

        ImageView IstIm = (ImageView)v.findViewById(R.id.IstIm);

        IstIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/istishev")));
            }
        });


        ImageView VedIm = (ImageView)v.findViewById(R.id.VedIm);

        VedIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/borisvedensky")));
            }
        });
        return v;
    }
}
