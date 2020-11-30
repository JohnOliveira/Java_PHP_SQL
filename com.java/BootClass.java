package com.syrinxsoft.riocarioca;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootClass extends BroadcastReceiver //Class OK!!!
{
    @Override
    public void onReceive(Context context,Intent intent)
    {
        intent = new Intent(context.getApplicationContext(), ServiceClass.class);
        context.startService(intent);
    }
}