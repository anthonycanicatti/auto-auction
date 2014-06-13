package edu.fordham.cis.autoauction;

import java.util.ArrayList;
import java.util.UUID;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	ArrayList<BluetoothDevice> nearbyDevices = new ArrayList<BluetoothDevice>();
	
	RadioButton mSellerButton;
	RadioButton mBuyerButton;
	TextView priceText;
	EditText mPriceEdit;
	Button mSubmitButton;
	
	BluetoothAdapter mBluetoothAdapter;
	
	// false for buyer, true for seller
	boolean bsStatus = false;
	
	UUID mUuid;
	
	int price = 0;
	
	ProgressDialog mProgressDialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        
        mSellerButton = (RadioButton)findViewById(R.id.sellerButton);
        mBuyerButton = (RadioButton)findViewById(R.id.buyerButton);
        priceText = (TextView)findViewById(R.id.pricetext);
        mPriceEdit = (EditText)findViewById(R.id.priceEdit);
        mSubmitButton = (Button)findViewById(R.id.submitButton);
        
        mBuyerButton.setChecked(true);
        setWidgetVisibility(false);
        
        mSellerButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
				setWidgetVisibility(isChecked);
				bsStatus = isChecked;
			}
        	
        });
        
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(bsStatus){
					
					String priceStr = mPriceEdit.getText().toString();
					
					if(!priceStr.isEmpty())
						price = (int)(100*Double.parseDouble(priceStr));
					else{
						Toast.makeText(getApplicationContext(), "Enter a valid seller price.", Toast.LENGTH_SHORT).show();
						return;
					}
					Log.i("PRICE", "Price in pennies: "+price);
				}
				
				
				loadConnection();
			}
		});
        

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
        	Log.i("MainActivity-onCreate", "Bluetooth Not Supported");
        	System.exit(0);
        }
        
        if(!mBluetoothAdapter.isEnabled()){
        	Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableBluetooth, 1);
        }
        
        
    }
    
    private void loadConnection(){
    	
    	new LoadBluetoothConnection().execute();
    	
    }
    
    private class LoadBluetoothConnection extends AsyncTask<Void, Void, Void> {

    	@Override
    	protected void onPreExecute(){
    		
    		Log.i("LoadBluetoothConnection-onPreExecute", "onPreExecute launched");
    		
    		mProgressDialog = new ProgressDialog(MainActivity.this);
    		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		mProgressDialog.setTitle("Loading");
    		mProgressDialog.setMessage("Awaiting Bluetooth Connection...");
    		mProgressDialog.setCancelable(false);
    		
    		mProgressDialog.show();
    		
    	}
    	
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			// BLUETOOTH WORK //
			
			
			if(!bsStatus){ // You're a buyer
				
				discoverDevices();
			}
			else{ // You're a seller
				
				final BluetoothServerSocket mServerSocket;
				BluetoothServerSocket temp = null;
				
				
				
			}
			
			return null;
		}
    	
    }
    
    private void setWidgetVisibility(boolean visible){
    	
    	if(!visible){
    		priceText.setVisibility(View.INVISIBLE);
            mPriceEdit.setVisibility(View.INVISIBLE);
    	}
    	else{
    		priceText.setVisibility(View.VISIBLE);
            mPriceEdit.setVisibility(View.VISIBLE);
    	}
    }
    
    private void discoverDevices(){
    	
    	mBluetoothAdapter.startDiscovery();
    	
    	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    	registerReceiver(mReceiver, filter);
    	
    }
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
    	
    	public void onReceive(Context context, Intent intent){
    		
    		String action = intent.getAction();
    		if(action.equals(BluetoothDevice.ACTION_FOUND)){
    			
    			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    			nearbyDevices.add(device);
    			
    			Log.i("Found device!", "Device info: NAME: "+device.getName()+"; ADDRESS: "+device.getAddress());
    		}
    	}
    };


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

}
