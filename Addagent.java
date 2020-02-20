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

public class Addagent extends Agent {
    private Context context;
    private String agentName = "Addagent";
    public String operation;
    float mValueOne, mValueTwo, mresult;

    protected void setup() {

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            mValueOne = (float) args[0];
            mValueTwo = (float) args[1];
        }
        addBehaviour(new Addition());
    }

    protected void takeDown() {
    }

    public class Addition extends OneShotBehaviour {
        public void action() {
            mresult = mValueOne + mValueTwo;
            Intent broadcast = new Intent();

            broadcast.setAction("com.example.Broadcast");
            broadcast.putExtra("mresult", mresult);
            context.sendBroadcast(broadcast);


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

