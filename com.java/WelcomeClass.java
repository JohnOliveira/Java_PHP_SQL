package com.syrinxsoft.riocarioca;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class WelcomeClass extends AppCompatActivity //Class OK!!!
{
    //Data base for don't open service more than once
    private SQLiteDatabase databaseFromWelcome;
    private String sqlFromWelcome;
    private Cursor cursorFromWelcome;

    //Buttons to call each window
    private ImageButton bt_beer, bt_info, bt_location;

    //AdMob
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_class);

        //AdMob
        MobileAds.initialize
            (getApplicationContext(), "ca-app-pub-8645172095912647~6682254891");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //OBS: When ADMOB is in tests, use 2 IDs, when is published, use only one.
        //EXAMPLE JAVA ca-app-pub-3940256099942544~3347511713
        //EXAMPLE XML ca-app-pub-3940256099942544/6300978111
        //AdMob

        bt_location = findViewById(R.id.bt_location);
        bt_info = findViewById(R.id.bt_info);
        bt_beer = findViewById(R.id.bt_beer);

        bt_location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { CallMapsActivity(); }
        });
        bt_info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { CallAboutActivity(); }
        });
        bt_beer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { CallBeerDisplay(); }
        });

        //Load Service If Don't Was Called
        DataBaseWelcome();
        LoadDataWelcome();
    }

    //Load Service If Don't Was Called
    private void DataBaseWelcome()
    {
        //For use in future...
        //getApplicationContext().deleteDatabase("welcome_db");

        //Create Data Base
        databaseFromWelcome = openOrCreateDatabase("welcome_db", MODE_PRIVATE, null);
        sqlFromWelcome = "CREATE TABLE IF NOT EXISTS welcome_table (welcome);";
        databaseFromWelcome.execSQL(sqlFromWelcome);
    }
    //Load Data
    private boolean LoadDataWelcome()
    {
        //Load Data Base And Check If Service Is Running
        cursorFromWelcome = databaseFromWelcome.query
        ("welcome_table", new String[]{"welcome"},
        null, null, null, null, null);

        if (cursorFromWelcome.getCount() != 0)
        {
            cursorFromWelcome.moveToLast();
            return true;
        }
        else
        {
            //Call service when is started for the first time
            CallServiceClassOnce();

            new AlertDialog.Builder(this).setTitle("Rio Carioca")
            .setMessage("Obrigado por baixar o aplicativo da Cerveja Rio Carioca. " +
            "Ao clicar em continuar você concorda com os termos de uso do aplicativo, " +
            "como sua geolocalização e transferência de dados com o servidor para atualizar " +
            "novos pontos de venda. Com nosso aplicativo, além estar sempre atualizado sobre " +
            "os nossos pontos de venda e onde encontrar o espirito carioca engarrafado, em breve, " +
            "você desfrutará de outras novidades e poderá ainda entrar em contato conosco e com os " +
            "desenvolvedores do App. Mantenha seu aplicativo sempre atualizado.\n" +
            "Divirta-se e, um brinde com o espirito carioca engarrafado!")

            .setNeutralButton("Continuar", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                DataBaseWelcome();
                SaveWelcomeColumn();
                }
            })
            .setIcon(R.mipmap.icon)
            .setCancelable(false)
            .show();
            return false;
        }
    }
    //Save Data
    private void SaveWelcomeColumn()
    {
        sqlFromWelcome = "INSERT INTO welcome_table (welcome) values ('saved')";
        databaseFromWelcome.execSQL(sqlFromWelcome);
        databaseFromWelcome.close();
    }
    //Call Maps Activity
    private void CallMapsActivity()
    {
        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(i);
    }
    //Call About
    private void CallAboutActivity()
    {
        Intent i = new Intent(getApplicationContext(), AboutClass.class);
        startActivity(i);
    }
    //Call Beer Display
    private void CallBeerDisplay()
    {
        Intent i = new Intent(getApplicationContext(), BeerDisplay.class);
        startActivity(i);
    }
    //Call Beer Display
    private void CallShare()
    {
        Intent i = new Intent(getApplicationContext(), Share.class);
        startActivity(i);
    }
    //Call Service Once If Start App For The First Time
    private void CallServiceClassOnce()
    {
        Context context = this;
        Intent intent = new Intent(context.getApplicationContext(), ServiceClass.class);
        context.startService(intent);
    }

    //OVERRIDE METHODS
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.welcome_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //case R.id.about_developer: return true;
            case R.id.share_button: CallShare();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        finish();
    }
}