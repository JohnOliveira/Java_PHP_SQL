package com.syrinxsoft.riocarioca;

import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class BeerDisplay extends AppCompatActivity
{
    private String pilsen, trigo, witbeer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_display);

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));

        pilsen =
        "Bohemian Pilsner\n\n"+
        "Álcool(%): 4,5 ABV\n"+
        "Amargor: 18 IBU\n"+
        "Ingredientes: Água, malte de cevada, lúpulo e levedura.\n"+
        "Temperatura: 0-4°C\n\n"+
        "Uma cerveja especial, que faz referência às melhores pilsners tchecas e alemãs, " +
        "sem deixar a desejar em nada, esta cerveja irá mudar seu conceito sobre este clássico estilo. " +
        "Preparada com puro malte importado e uma combinação de lúpulos especiais, é suavemente amarga, " +
        "especialmente saborosa e de cor dourada, com creme branco e aveludado. É uma cerveja de baixa " +
        "fermentação que agrada à todos os paladares. Harmoniza com carne vermelha grelhada, carnes brancas, " +
        "queijos leves, saladas, pizzas e sanduíches. Uma cerveja para todos os momentos.";

        trigo =
        "Weizenbier\n\n"+
        "Álcool(%): 4,5 ABV\n"+
        "Amargor: 15 IBU\n"+
        "Ingredientes: Água, malte de cevada, malte de trigo, lúpulo e levedura.\n"+
        "Temperatura: 5-7°C\n\n"+
        "Cerveja refrescante, de alta fermentação e paladar suave. Apresenta notas de cravo, " +
        "banana e aromas frutados. É produzida com fermento alemão específico para cervejas de trigo, " +
        "o que define os aromas de banana e cravo, muito típicos do estilo, proporcionando excelente " +
        "carbonatação natural. A coloração é amarela e turva, e a espuma é consistente e densa. " +
        "Harmoniza bem com carnes em geral, massas, pizzas e saladas, além de queijos e frios";

        witbeer =
        "Witbier\n\n"+
        "Álcool(%): 4,6 ABV\n"+
        "Amargor: 16 IBU\n"+
        "Ingredientes: Água, malte de cevada, malte de trigo, trigo não maltado, " +
        "erva-mate, limão-siciliano, lúpulo e levedura.\n"+
        "Temperatura: 3-7°C\n\n"+
        "A Cerveja Rio Carioca Malte Limão é leve e elegante, de alta fermentação, com uma base de malte " +
        "de cevada, malte de trigo e trigo não maltado. Ela é clara e turva, com boa formação de espuma, leve " +
        "criticidade e não filtrada, o que garante aromas frescos oriundos da adição de limão-siciliano e da erva-mate. " +
        "A Rio Carioca Malte Limão harmoniza muito bem com comidas leves, como saladas, peixes, ricota e risotos. Também " +
        "é uma excelente companhia para um Ceviche ou mesmo o bom e velho biscoito de polvilho praiano. É a cerveja que tem " +
        "vocação nata para as grandes comemorações.";

        //Create a back button in ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Make this class to close by click in back button
    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
    @Override
    public void onBackPressed() { super.onBackPressed(); finish(); }

    public void PilsenMethod(View v) { InfoScreen("Pilsen Responsa", pilsen); }
    public void TrigoMethod(View v) { InfoScreen("Trigo Especial", trigo); }
    public void WitbeerMethod(View v) { InfoScreen("Malte Limão", witbeer); }

    //InfoScreen
    private void InfoScreen(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(BeerDisplay.this, R.style.MyAlertDialogStyle);
        builder.setTitle(title)
        .setMessage(message)
        .setNeutralButton("Fechar", new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which) {}
        })
        //.setIcon(R.mipmap.icon)
        .setCancelable(false)
        .show();
    }

    //OVERRIDE METHODS
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.beer_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.about_beer: AlertBeer(); return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void AlertBeer()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(BeerDisplay.this);
        builder.setTitle("Modo de uso")
        .setMessage("Deslize a tela para visualizar todas as cervejas.\n\n" +
            "Clique na garrafa para abrir as informações.")
        .setNeutralButton("Fechar", new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which) {}
        })
        //.setIcon(R.mipmap.icon)
        .setCancelable(false)
        .show();
    }
}