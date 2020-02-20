package calculator1;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import calculator1.R;

import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import android.util.Log;
        import java.net.InetAddress;
        import java.net.NetworkInterface;
        import java.net.SocketException;
        import java.util.Enumeration;
        import android.net.ConnectivityManager;
import jade.util.Logger;
        import android.content.ComponentName;
        import android.content.ServiceConnection;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.os.Bundle;
        import android.os.IBinder;
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
        import jade.core.Profile;

        import jade.android.AgentContainerHandler;
        import jade.android.AgentHandler;
        import jade.android.RuntimeCallback;
        import jade.android.RuntimeService;
        import jade.android.RuntimeServiceBinder;
        import jade.wrapper.StaleProxyException;
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
        import jade.util.leap.Properties;



        import java.net.InetAddress;
        import java.net.NetworkInterface;
        import java.net.SocketException;
        import java.util.Enumeration;


public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "MainActivity";
    EditText etNum1;
    EditText etNum2;
    Button btnAdd;
    Button btnSub;
    Button btnMult;
    Button btnDiv;
    Boolean isBound;
    TextView tvResult;
    int opr=0;
    int num1 = 0;
    int num2 = 0;
    int result = 0;
    ServiceConnection myConnection;
    String oper = "";


    private Context context;
    private calculator1.MainActivity myActivity;
    private ServiceConnection serviceConnection;
    private MicroRuntimeServiceBinder microRuntimeServiceBinder;
    private TextView logConsole;
  //  public TextView lStatoSistema;
 // public PhoneAgent agent;





  //  float mValueOne , mValueTwo ;

   // boolean mAddition , mSubtract ,mMultiplication ,mDivision ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //  logConsole = (TextView) findViewById(R.id.log_console);


        // find the elements
        etNum1 = (EditText) findViewById(R.id.etNum1);
        etNum2 = (EditText) findViewById(R.id.etNum2);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSub = (Button) findViewById(R.id.btnSub);
        btnMult = (Button) findViewById(R.id.btnMult);
        btnDiv = (Button) findViewById(R.id.btnDiv);
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnMult.setOnClickListener(this);
        btnMult.setOnClickListener(this);
        tvResult = (TextView) findViewById(R.id.tvResult);


        context = getApplicationContext();


        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
// Bind successful
                // Log.i("service connected","done");
                microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;


                bindService(new Intent(getApplicationContext(), MicroRuntimeService.class), serviceConnection, Context.BIND_AUTO_CREATE);

                // Successfully bound to the JADE MicroRuntimeService.
                // Now you can start and stop JADE and create agents on it
                // through the methods of the MicroRuntimeServiceBinder class

                Properties pp = new Properties();
                pp.setProperty(Profile.MAIN_HOST, "localhost");
                pp.setProperty(Profile.MAIN_PORT, "1099");
                pp.setProperty(Profile.JVM, Profile.ANDROID);


                microRuntimeServiceBinder.startAgentContainer(pp, new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void thisIsNull) {

                        Log.i(TAG,"Agent container created");
                    }





                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("agent container startup unsuccessful");
                    }

                });
            }
            public void onServiceDisconnected(ComponentName className) {
                microRuntimeServiceBinder = null;
            }
        };


                                                                                                                    }
    protected void onDestroy()
    {
        disconnectFromJade();
    }
    private void disconnectFromJade()
    {
        if(microRuntimeServiceBinder != null)
        {
            microRuntimeServiceBinder.stopAgentContainer(new RuntimeCallback<Void>()
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

    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.add: {

                int num1 = Integer.parseInt(etNum1.getText().toString());
                int num2 = Integer.parseInt(etNum2.getText().toString());
                opr=1;
                Object[] args = new Object[3];
                args[0] = num1;
                args[1] = num2;
                args[2] = opr;

                microRuntimeServiceBinder.startAgent("commuteagent", "agent.commuteagent",args,new RuntimeCallback<Void>() {
                    @Override
                    public void onSuccess(Void thisIsNull) {
                        Log.i(TAG,"Agent created");
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.i(TAG,"Agent not created");
                    }
                });
            }
            break;
           /* case R.id.subtract:
            {

            }
            break;
            case R.id.multi:
            {
            }
            break;*/
        }
        // form the output line
      /*  tvResult.setText(num1 + " " + oper + " " + num2 + " = " + result);
    }
*/
    }
}



