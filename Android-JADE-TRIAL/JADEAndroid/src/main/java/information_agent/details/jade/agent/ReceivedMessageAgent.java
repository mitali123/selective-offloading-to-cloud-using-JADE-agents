package information_agent.details.jade.agent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import information_agent.details.jade.maincontainer.*;
import information_agent.details.jade.utils.*;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;
import android.content.*;

import java.util.ArrayList;
import java.util.List;

public class ReceivedMessageAgent extends Agent implements SimpleAgentInterface {



    private static final long serialVersionUID = 1594371294421614291L;
    private Set participants = new SortedSetImpl();
    //private Codec codec = new SLCodec();
    private Context context;
    private String ipAddress = "127.0.0.1"; // running on localhost server for now
    private String agentName = "android-agent";
    private MessageTemplate mt;
    private static final String TAG = "ReceivedMessage"; // constants

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof Context) {
                context = (Context) args[0];
            }
        }

        // Add initial behaviours
        addBehaviour(new ParticipantsManager(this));

        // Activate the GUI
        registerO2AInterface(SimpleAgentInterface.class, this);

        Intent broadcast;
        broadcast = new Intent();
        // broadcast.setAction("jade.demo.agent.SEND_MESSAGE");


       /* System.out.println("Agent "+getLocalName()+": waiting for REQUEST message...");
        //ACLMessage msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        System.out.println("Agent "+getLocalName()+": REQUEST message received. Reply and exit.");
        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
        //reply.addReceiver(msg.getSender());
        reply.setContent("exiting");
        send(reply);
*/

        //String[] arr=new String[100];
        AID dummy_agent;
        dummy_agent = new AID();
        //ay1 String[] =new array1;

       // dummy_agent.
        //List<String> add=new ArrayList<>();
        //add=dummy_agent.getAddressesArray();
        //for(a:String )
        broadcast.setAction("jade.demo.agent.RECEIVE_MESSAGE");
        Log.i(TAG, "#### -- Recieving broadcast " + broadcast.getAction());
        MyReceiver myReceiverObject = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("jade.demo.chat.REFRESH");
        context.registerReceiver(myReceiverObject, filter);
        // context.sendBroadcast(broadcast);
        // context.registerReceiver()
        //Get ipAddress and agentName
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.PREFS_HOST_ADDRESS, ipAddress);
        agentName = sharedPreferences.getString(Constants.PREFS_AGENT_NAME, agentName);
    }

    protected void takeDown() {
    }

    class ParticipantsManager extends CyclicBehaviour {
        private static final long serialVersionUID = -4845730529175649756L;

        ParticipantsManager(Agent a) {
            super(a);
            // calling superclass constructor
        }

        public void onStart() {
            //Start cyclic
        }


        public void action() {
            // Listening for incoming message

            Log.i(TAG, "Listening for incoming messsage from Agent");
            Log.i(TAG, " messsage has been received by the Agent !");
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // handling exceptions if any
                //Log.i(TAG, " messsage has been received by the Agent !"); // try this if first one doesnt work.
                try {
                    //Get message
                    String message = msg.getContent();
                    Log.i(TAG, " messsage has been received");
                    Log.i(TAG, "###Incoming message:" + message);
                    exportLog("Incoming message:" + message);

                } catch (Exception e) {
                    Log.i(TAG, " messsage has not been received");
                    Logger.println(e.toString());
                    e.printStackTrace();
                }
            } else {
                // if msg is NULL we bock it
                Log.i(TAG, " messsage is NULL !");
                block();
            }
        }
    } // END of inner class ParticipantsManager




    public String[] getParticipantNames() {
        String[] pp = new String[participants.size()];
        return pp;
    }

    public void onHostChanged(String host) {
        ipAddress = host;
    }

    public void onAgentNameChanged(String name) {
        agentName = name;
    }

    private void exportLog(String log) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.exportLogConsole(log);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String a = intent.getAction();
            if (a.equals("jade.demo.chat.REFRESH")) {
                String t = intent.getStringExtra("msg");

            }
        }
    }
}