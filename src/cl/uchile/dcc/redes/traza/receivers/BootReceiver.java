package cl.uchile.dcc.redes.traza.receivers;

import cl.uchile.dcc.redes.traza.services.TrazaService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(!TrazaService.isRunning()){
			Intent trazaServiceIntent = new Intent(context, TrazaService.class);
	    	context.startService(trazaServiceIntent);
		}
	}

}
