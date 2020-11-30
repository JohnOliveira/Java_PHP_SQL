package com.syrinxsoft.riocarioca;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class AboutClass extends AppCompatActivity //OK
{
    private ImageButton siteSyrinx, siteBeer, emailSyrinx, fbBeer, twiBeer, insBeer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        siteSyrinx = findViewById(R.id.siteSyrinx); siteBeer = findViewById(R.id.siteBeer);
        emailSyrinx = findViewById(R.id.emailSyrinx);
        fbBeer = findViewById(R.id.fbBeer);
        twiBeer = findViewById(R.id.twiBeer);
        insBeer = findViewById(R.id.insBeer);

        //Site Syrinx and Beer
        siteSyrinx.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) { URIMethod("http://www.syrinxsoft.com"); }
        });
        siteBeer.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) { URIMethod("http://www.cervejariocarioca.com.br"); }
        });
        //Email Syrinx and Beer
        emailSyrinx.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: support@syrinxsoft.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Aplicativo Rio Carioca");
            startActivity(Intent.createChooser(emailIntent, "Enviar email"));
            }
        });
        /*emailBeer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto: contato@cervejariocarioca.com.br"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Aplicativo Rio Carioca");
                startActivity(Intent.createChooser(emailIntent, "Enviar email"));
            }g
        });*/
        //Facebook, Twitter, Instagram
        fbBeer.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) { URIMethod("http://www.facebook.com/cervejariocarioca"); }
        });
        twiBeer.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) { URIMethod("https://twitter.com/cervariocarioca"); }
        });
        insBeer.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view) { URIMethod("https://instagram.com/Cervejariocarioca"); }
        });

        //Create a back button in ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void URIMethod(String url)
    {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
    //Make this class to close by click in back button
    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
    @Override
    public void onBackPressed() { super.onBackPressed(); finish(); }
}