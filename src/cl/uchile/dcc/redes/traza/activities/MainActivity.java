package cl.uchile.dcc.redes.traza.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;
import cl.uchile.dcc.redes.traza.R;
import cl.uchile.dcc.redes.traza.services.TrazaService;


public class MainActivity extends Activity {

	private SharedPreferences settings;
	
	public static final String[] companies = new String[] {"Entel", "Movistar", "Nextel", "Virgin Mobile", "VTR Móvil"};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
        	getFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
        	if(!settings.contains("company")){
        		CompanyDialogFragment dialog = new CompanyDialogFragment();
        		dialog.setCancelable(false);
        		dialog.show(getFragmentManager(), "Caca");
        	}
        }
    }
    
    protected void caca(){
    	CompanyDialogFragment dialog = new CompanyDialogFragment();
		dialog.show(getFragmentManager(), "Caca");
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainFragment extends Fragment {

        public MainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
        
        @Override
        public void onResume() {
        	super.onResume();
        	ToggleButton toggleButton = (ToggleButton)getActivity().findViewById(R.id.toggleService);
        	toggleButton.setChecked(TrazaService.isRunning());
        }
    }
    
    public static class CompanyDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Selecciona tu operador")
				.setItems(companies , new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
						settings.edit().putInt("company", which).commit();
						getDialog().dismiss();
					}
				});
			// Create the AlertDialog object and return it
			return builder.create();
		}
		
		@Override
		public void onResume() {
			super.onResume();
			getDialog().setCanceledOnTouchOutside(false);
		}
	}
    
    private void startService() {
    	Intent intent = new Intent(this, TrazaService.class);
    	startService(intent);
    }
    
    private void stopService() {
    	Intent intent = new Intent(this, TrazaService.class);
    	stopService(intent);
    }
    
    public void onClickToggleButton(View view) {
    	// Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();
        
        if (on) {
            startService();
        } else {
            stopService();
        }
    }
}