package agent;

/**
 * Created by administrator on 12/2/17.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import calculator1.MainActivity;
import calculator1.MainActivity.*;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.content.*;
import calculator1.MainActivity.*;
import jade.android.RuntimeCallback;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;

import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

public class commuteagent extends Agent {
    private Context context;
    private String agentName = "commuteagent";
    public String operation;
   int num1,num2,opr,result;

    protected void setup() {

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            String arg1 = args[0].toString();
            String arg2 = args[1].toString();
            String arg3 = args[2].toString();
        }
        addBehaviour(new commute());
    }

    protected void takeDown() {
    }

    public class commute extends OneShotBehaviour {
        public void action() {

            /*Intent broadcast = new Intent();

            broadcast.setAction("com.example.Broadcast");
            broadcast.putExtra("result", result);
            context.sendBroadcast(broadcast);*/


        }
    }

    private class MyReceiver
            extends BroadcastReceiver {
        @Override
        public void onReceive(Context context,
                              Intent intent) {
            String a = intent.getAction();
            if (a.equals("com.example.Broadcast")) {
                String t = intent.getStringExtra("mresult");

            }
        }
    }
}

