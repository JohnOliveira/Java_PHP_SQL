package com.syrinxsoft.riocarioca;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServiceClass extends Service //Class OK!!!
{
    //Variables used in connection
    private GETConnection getConnection;
    private URL url;
    private String loginUrl;
    private HttpURLConnection httpURLConnection;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private Context cont;
    private String result;//Change the name after
    private String line;//Change the name after
    private char lastInRow;
    private int totalPlaces, listIndex, skipTheLists;
    private char currentCharacter;
    private String storageBits;

    //Variables to storage all data from Database
    private SQLiteDatabase[] placesDB;
    private String stringPlacesDB;

    //Compare if there is new place
    private SQLiteDatabase compareDB;
    private String stringCompareDB;
    private Cursor cursorCompareDB;
    private int pi;

    //Variables to pass info places to map
    private String[] lat_list;
    private String[] lng_list;
    private String[] names_list;
    private String[] address_list;
    private String[] number_list;
    private String[] available_list;

    //Variables to compare positions NOT IN USE
    //private SQLiteDatabase databaseForPositions; NOT IN USE
    //private String sqlForPositions; NOT IN USE
    //private Cursor cursorForPositions; NOT IN USE
    //private String positionsString; NOT IN USE
    //private int positionsInt; NOT IN USE

    //Variables for Timer
    private Handler handler;

    @Override//With no use
    public IBinder onBind(Intent intent) { return null; }

    @Override//Run once at start service
    public void onCreate()
    {
        super.onCreate();
    }

    @Override//Run and restart service if itself close
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        doFirstWork();

        return (START_STICKY);
    }

    //Timer to keep verification in DB
    void doFirstWork()
    {
        handler = new Handler();

        handler.postDelayed(new Runnable()
        {
            public void run() { doNextWork(); }
        }, 9000);
    }
    void doNextWork()
    {
        getConnection = new GETConnection(getApplicationContext());
        getConnection.execute("http://syrinxsoft.com/rio_carioca_beer_db/show.php");

        doFirstWork();
    }

    //CONNECTION WITH PHPMyAdmin
    class GETConnection extends AsyncTask<String, Void, String>
    {
        GETConnection(Context c)
        {
            cont = c;
        }

        @Override
        protected String doInBackground(String... params)
        {
            loginUrl = params[0];
            try
            {
                //Connect to DB
                url = new URL(loginUrl);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                outputStream = httpURLConnection.getOutputStream();
                outputStream.close();
                inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                result = "";

                // Read each bit in the DataBase
                while ((line = bufferedReader.readLine()) != null)
                {
                    result += line;
                }
                bufferedReader.close(); inputStream.close(); httpURLConnection.disconnect();

                // Loop to return total of places registered
                lastInRow = '!'; totalPlaces = 0;
                for (int i = 0; i < result.length(); i++)
                {
                    if (lastInRow == result.charAt(i)) { totalPlaces++; }
                }

                //Check places before read code below
                CompareDB();
                CompareDBLoad();

                return result;
            }
            catch (MalformedURLException e) { e.printStackTrace(); }
            catch (IOException e) { e.printStackTrace(); }

            return null;
        }
        @Override protected void onPreExecute() { super.onPreExecute(); }
        @Override protected void onPostExecute(String string) { /*super.onPostExecute(aVoid);*/ }
        @Override protected void onProgressUpdate(Void... values) { super.onProgressUpdate(values); }
    }
    //Create DB for places
    private void CreatePlacesDB()
    {
        for (int i = 0; i < totalPlaces; i++)
        {
            placesDB[i] = openOrCreateDatabase("places" + i, MODE_PRIVATE, null);
            stringPlacesDB = "CREATE TABLE IF NOT EXISTS p (lat, lng, name, address, number, available);";
            placesDB[i].execSQL(stringPlacesDB);
        }
    }
    //Save DB for places
    private void SavePlacesDB()
    {
        for (int i = 0; i < totalPlaces; i++)
        {
            stringPlacesDB = "INSERT INTO P (lat, lng, name, address, number, available) values"
+ "('"+lat_list[i]+"','"+lng_list[i]+"','"+names_list[i]+"','"+address_list[i]+"','"+number_list[i]+"','"+available_list[i]+"')";
            placesDB[i].execSQL(stringPlacesDB);
            placesDB[i].close();
        }
    }
    //==============================================================================================
    //To compare total places in DB
    private void CompareDB()
    {
        compareDB = openOrCreateDatabase("c_db", MODE_PRIVATE, null);
        stringCompareDB = "CREATE TABLE IF NOT EXISTS c_table (compare);";
        compareDB.execSQL(stringCompareDB);
    }
    private void CompareDBSave()
    {
        stringCompareDB = "INSERT INTO c_table (compare) values ('"+ totalPlaces +"')";
        compareDB.execSQL(stringCompareDB);
        compareDB.close();
    }
    private boolean CompareDBLoad()
    {
        cursorCompareDB = compareDB.query("c_table", new String[]{"compare"},
                null, null, null, null, null);

        if (cursorCompareDB.getCount() != 0)
        {
            cursorCompareDB.moveToLast();

            String p = cursorCompareDB.getString(cursorCompareDB.getColumnIndex("compare"));
            pi = Integer.parseInt(p);

            if (totalPlaces > pi)
            {
                CompareDBSave();
                DBToMap();

                CreatePlacesDB();
                SavePlacesDB();

                AddressNotification();
            }
            else if(pi > totalPlaces)
            {
                CompareDBSave();
                DBToMap();

                CreatePlacesDB();
                SavePlacesDB();
            }
            else
            {
                CompareDBSave();
                DBToMap();

                CreatePlacesDB();
                SavePlacesDB();       Log.i("IN SERVICE", "UPDATING...");
            }

            return true;
        }
        else
        {
            CompareDBSave();
            DBToMap();

            CreatePlacesDB();
            SavePlacesDB();

            return false;
        }
    }
    //Send data saved in Service to Map
    private void DBToMap()
    {
        //To be read only if there is a new place
        placesDB = new SQLiteDatabase[totalPlaces];
        lat_list = new String[totalPlaces];//OK
        lng_list = new String[totalPlaces];//OK
        names_list = new String[totalPlaces];//OK
        address_list = new String[totalPlaces];//OK
        number_list = new String[totalPlaces];//OK
        available_list = new String[totalPlaces];//OK

        //Separate each data and sent to correspondent list
        currentCharacter = result.charAt(0);
        storageBits = "";
        listIndex = 0;
        skipTheLists = 0;
        for (int i = 0; currentCharacter != '@'; i++)//To put each word in list index
        {
            currentCharacter = result.charAt(i);
            if (currentCharacter != '#')
            {
                storageBits += (char)currentCharacter;

                //This IF is called when PHP get in last ECHO
                if (currentCharacter == '!')
                {
                    skipTheLists = 0;
                    listIndex++;
                    storageBits = "";
                }
            }
            else
            {
                if (skipTheLists == 0)
                    lat_list[listIndex] = "" + storageBits;
                if (skipTheLists == 1)
                    lng_list[listIndex] = "" + storageBits;
                if (skipTheLists == 2)
                    names_list[listIndex] = "" + storageBits;
                if (skipTheLists == 3)
                    address_list[listIndex] = "" + storageBits;
                if (skipTheLists == 4)
                    number_list[listIndex] = "" + storageBits;
                if (skipTheLists == 5)
                    available_list[listIndex] = "" + storageBits;

                skipTheLists++;
                storageBits = "";
            }
        }
    }

    //Notification to inform user when there are new places
    public void AddressNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.iconin);
        builder.setContentTitle(names_list[names_list.length - 1]);//names.get(names.size()-1));
        builder.setContentText(address_list[address_list.length - 1]);//address.get(address.size()-1));
        builder.setTicker("Novo ponto de venda!");

        //Open specifically class by press in notification
        Intent resultIntent = new Intent(this, MapsActivity.class);
        resultIntent.putExtra("Notify",true);
        PendingIntent resultPendingIntent = PendingIntent.getActivity
                (this,0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);
        //-----------------------------------------------------------------------------------
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(9999, builder.build());

        //To use later...
        //int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        //manager.notify(m, builder.build());
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}