package com.syrinxsoft.riocarioca;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Share extends AppCompatActivity
{
    private ImageButton whatsapp;
    private ImageButton facebook;
    private ImageButton twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);

        //TwitterAuthConfig authConfig =  new TwitterAuthConfig("consumerKey", "consumerSecret");
        //Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());

        whatsapp = findViewById(R.id.whatsapp_share);
        facebook = findViewById(R.id.facebook_share);
        twitter = findViewById(R.id.twitter_share);

        whatsapp.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v) { WhatsAppShare(); }
        });
        facebook.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v) { FacebookShare(); }
        });
        twitter.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v) { TwitterShare(); }
        });

        //Create a back button in ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //Make this class to close by click in back button
    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
    @Override
    public void onBackPressed() { super.onBackPressed(); finish(); }

    private void WhatsAppShare()
    {
        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        waIntent.setPackage("com.whatsapp");
        if (waIntent != null)
        {
            waIntent.putExtra
                    (Intent.EXTRA_TEXT, "Ei, já tomou uma Rio Carioca hoje!? Procure já a sua " +
                            "no aplicativo Cerveja Rio Carioca.\n\n" +
                            "Android\n" + "https://goo.gl/k0ZgK6" +"\niOS\n"+ "https://goo.gl/IXlHcJ");

            startActivity(Intent.createChooser(waIntent, "Share with"));
        }
        else
            Toast.makeText(Share.this, "WhatsApp não instalado", Toast.LENGTH_SHORT).show();
    }

    private void FacebookShare()
    {
        Intent face = new Intent(Intent.ACTION_SEND);
        face.setType("text/plain");
        face.setPackage("com.facebook.katana");
        face.putExtra(Intent.EXTRA_TEXT, "https://goo.gl/eOy3OQ");
        startActivity(Intent.createChooser(face, "Compartilhar com..."));
    }

    private void TwitterShare()
    {
        Intent twitter = new Intent(Intent.ACTION_SEND);
        twitter.setType("text/plain");
        twitter.setPackage("com.twitter.android");
        if (twitter != null)
        {
            twitter.putExtra(Intent.EXTRA_TEXT, "Ei, já tomou uma Rio Carioca hoje!? Procure já a sua " +
                    "no app Cerveja Rio Carioca\n\n" +
                                    "Android-" + "https://goo.gl/k0ZgK6" +"\niOS-"+ "https://goo.gl/IXlHcJ");
            startActivity(Intent.createChooser(twitter, "Compartilhar com..."));
        }
        else
            Toast.makeText(Share.this, "Twitter não instalado", Toast.LENGTH_SHORT).show();
    }
}