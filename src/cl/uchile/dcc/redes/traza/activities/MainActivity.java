package cl.uchile.dcc.redes.traza.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;
import cl.uchile.dcc.redes.traza.R;
import cl.uchile.dcc.redes.traza.services.TrazaService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleService);
    	toggleButton.setChecked(TrazaService.isRunning());
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
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