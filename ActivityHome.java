package airportAgents.mobileAgent;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import airportAgents.supportClasses.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.OneShotBehaviour;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class ActivityHome extends Activity
{
	private Context context;
	private ActivityHome myActivity;
    private MicroRuntimeServiceBinder jadeServiceBinder;
    public ConnectionManager connectionManager;
    public TextView lStatoSistema;
    public PhoneAgent agent;
    private static final int MY_USR_PSW_DIALOG_ID = 1;
    private static final int MY_MAP_VIEW_DIALOG_ID = 2;
    private boolean firstTime;
    private static final String TAG = "HomeActivity";
    
    private final String hostIpAddr = "192.168.1.3";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        context = getApplicationContext();
        myActivity = this;
        firstTime = true;
        
        connectionManager = new ConnectionManager(context);
        
        if(connectionManager.isConnectedToNetwork())
        	connectToJade();
        
        // Registrazione dei Listener
        findViewById(R.id.bCloseHome).setOnClickListener(buttonListener);
        findViewById(R.id.bSistema).setOnClickListener(buttonListener);
        findViewById(R.id.bMap).setOnClickListener(buttonListener);
        
        lStatoSistema = (TextView)findViewById(R.id.lStatoSistema);
        
    } //onCreate() finish
    
    @Override
    protected void onStart() 
    {// The activity is about to become visible.
        super.onStart();
        GlobalState gs = (GlobalState) getApplication();
        gs.setActivityCurrentlyVisible(this);
        Log.i(TAG, "onStart()");
                
        if(!connectionManager.isConnectedToNetwork())
        {
        	Toast toast = Toast.makeText(myActivity, "Impossibile connettersi alla rete!", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
			findViewById(R.id.bMap).setEnabled(false);
			findViewById(R.id.bSistema).setEnabled(false);
        }
        else if(firstTime)
        {
        	((TextView)findViewById(R.id.lIpAddr)).setTextColor(Color.YELLOW);
        	((TextView)findViewById(R.id.lIpAddr)).setText(getipAddress());
        	showDialog(MY_MAP_VIEW_DIALOG_ID);
        }
    } //onStart() finish

    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
        GlobalState gs = (GlobalState) getApplication();
        gs.setCurrentLiveTask(null);
        disconnectFromJade();
    }//onDestroy() finish
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.homeItem:
            //don't make nothing
            return true;
        case R.id.tasksItem:
        	intent = new Intent(this, ActivityTask.class);
        	startActivity(intent);
            return true;
        case R.id.commercialItem:
        	intent = new Intent(this, ActivityCommercial.class);
        	startActivity(intent);
            return true;
        case R.id.FlightInfoItem:
        	intent = new Intent(this, ActivityFlight.class);
        	startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /* Implementazione dei Listener */
    private OnClickListener buttonListener = new OnClickListener()
    {
        public void onClick(View v)
        {
        	if(v.getId() == R.id.bCloseHome) //bCloseHome
        	{
        		if(agent != null)
        			agent.doDelete();
        		else
        			finish();
        	}
        	else if(v.getId() == R.id.bMap) //bMap
        	{
        		if(agent != null)
        		{
    				agent.addBehaviour(new OneShotBehaviour()
	        		{
						@Override
						public void action() 
						{
							ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
							msg.addReceiver(agent.phoneMapAgentAid);
			        		msg.setConversationId("phoneAgent open map");
			        		//msg.setContent(agent.pos_x+"\n"+agent.pos_y+"\n"+agent.connectedToTheSystem);
			        		msg.setContent(""+agent.connectedToTheSystem);
			        		agent.send(msg);
						}
	        		});
    			}
    			else
    			{
    				Toast toast = Toast.makeText(myActivity, "Impossibile aprire la mappa!", Toast.LENGTH_LONG);
    				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
    				toast.show();
    			}
        	}
        	else if(v.getId() == R.id.bSistema) //bSistema
        	{
        		if(agent != null)
        		{
        			if(!agent.connectedToTheSystem)
        				showDialog(MY_USR_PSW_DIALOG_ID); //lancio il popup per il login
        			else
        				agent.disconnectFromSystem();
        		}
    			else
    			{
    				Toast toast = Toast.makeText(ActivityHome.this, "Impossibile connettersi al sistema! Controllare la connessione.", Toast.LENGTH_LONG);
    				toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
    				toast.show();
    			}
			}
        }
    };
    
    protected Dialog onCreateDialog(int id)
    {
        Dialog dialog;
        LayoutInflater inflater;
        AlertDialog.Builder builder;
        switch(id)
        {
	        case MY_USR_PSW_DIALOG_ID:
        		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	
	        	final View layout_user_psw_dialog = inflater.inflate(R.layout.user_psw_dialog, (ViewGroup) findViewById(R.id.root_user_psw_dialog));
	        	final EditText username = (EditText) layout_user_psw_dialog.findViewById(R.id.edUsername);
	            final EditText password = (EditText) layout_user_psw_dialog.findViewById(R.id.edPassword);
	            final Button bConfirm = (Button) layout_user_psw_dialog.findViewById(R.id.bConfirm);
	            
	            bConfirm.setOnClickListener(new OnClickListener()
	            {
	            	@Override
	        		public void onClick(View v) 
	        		{
	            		String usr;
	            		String psw;
	            		if(username.getText().toString().equals(""))
	            			usr = "null";
	            		else
	            			usr = username.getText().toString();
	            		if(password.getText().toString().equals(""))
	            			psw = "null";
	            		else
	            			psw = password.getText().toString();
	            		agent.systemConnect(usr, psw);
	            		removeDialog(MY_USR_PSW_DIALOG_ID);
	        		}
	            });
	        	
	            builder = new AlertDialog.Builder(this);
	            builder.setTitle("Login");
	            builder.setView(layout_user_psw_dialog);
	            
	            AlertDialog passwordDialog = builder.create();
	            return passwordDialog;
	        case MY_MAP_VIEW_DIALOG_ID:
	        	firstTime = false;
        		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        	
	        	final View layout_map_view_dialog = inflater.inflate(R.layout.map_view_request_dialog, (ViewGroup) findViewById(R.id.root_map_view_request_dialog));
	        	final Button bConfirmMapViewDialog = (Button) layout_map_view_dialog.findViewById(R.id.bConfirmMapViewDialog);
	            
	        	String[] items = new String[2];
	        	items[0] = "partenze";
	        	items[1] = "arrivi";
	        	ArrayAdapter ad = new ArrayAdapter(this,android.R.layout.simple_spinner_item,items);
	            ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	            Spinner spin=(Spinner)layout_map_view_dialog.findViewById(R.id.sMapViewChose);
	            spin.setPrompt(getString(R.string.label_choose_map_view));
	            spin.setAdapter(ad);
	            
	            bConfirmMapViewDialog.setOnClickListener(new OnClickListener()
	            {
	            	@Override
	        		public void onClick(View v) 
	        		{
	            		if(agent != null)
	            		{
	            			agent.mapView = ((Spinner)layout_map_view_dialog.findViewById(R.id.sMapViewChose)).getSelectedItem().toString();
	            			agent.addB();
	            		}
	            		removeDialog(MY_MAP_VIEW_DIALOG_ID);
	        		}
	            });
	            builder = new AlertDialog.Builder(this);
	            builder.setTitle("Scelta mappa");
	            builder.setView(layout_map_view_dialog);
	            
	            AlertDialog mapViewdialog = builder.create();
	            return mapViewdialog;
	        default:
	            dialog = null;
        }
        
        return dialog;
    }
    
    /* Connect to Jade container */
    private void connectToJade()
    {
		ServiceConnection serviceConnection = new ServiceConnection()
		{
			public void onServiceConnected(ComponentName className, IBinder service)
			{
		    	jadeServiceBinder = (MicroRuntimeServiceBinder) service;
			    // Successfully bound to the JADE MicroRuntimeService.
			    // Now you can start and stop JADE and create agents on it
			    // through the methods of the MicroRuntimeServiceBinder class
			
				jadeServiceBinder.startAgentContainer(hostIpAddr, 1099, new RuntimeCallback<Void>() {
				
					@Override
					public void onSuccess(Void thisIsNull) 
					{
						// Successfully start of the JADE Split Container.
						// Manage your Agents here
						Object[] args = new Object[2];
						args[0] = myActivity;
						args[1] = (GlobalState) getApplication();
						jadeServiceBinder.startAgent("AndroidAgent", "airportAgents.mobileAgent.PhoneAgent", args, new RuntimeCallback<Void>() 
						{
							@Override
							public void onSuccess(Void thisIsNull) 
							{
								/* sistema rilevato ma non mi sono loggato */
								(new GUIUpdater()).setLabel(myActivity, true, false);
							}
								@Override
							public void onFailure(Throwable throwable) {//problemi nell'agent
								}
						});
					}
					@Override
					public void onFailure(Throwable throwable)
					{
						//problemi nel container
		      		}
		      	});
		  	  };
			
		  	  public void onServiceDisconnected(ComponentName className) {
		  	    jadeServiceBinder = null;
		  	  }
  		 };
	
	  	 context.bindService(new Intent(context, MicroRuntimeService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}
    
    private void disconnectFromJade()
    {
    	if(jadeServiceBinder != null)
        {
        	jadeServiceBinder.stopAgentContainer(new RuntimeCallback<Void>()
    		{
				@Override
				public void onFailure(Throwable arg0) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onSuccess(Void arg0){
					// TODO Auto-generated method stub
				}
    		});
        }
    }
    
    /* Get the ip address */
    private static String getipAddress()
    { 
        try 
        {
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) 
                    {
                        String ipaddress=inetAddress.getHostAddress().toString();
                        Log.e("ip address",""+ipaddress);
                        return ipaddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Socket exception in GetIP Address of Utilities", ex.toString());
        }
        return null; 
    }
}