package information_agent.details.jade.agent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import information_agent.*;
import information_agent.details.jade.maincontainer.MainActivity;
import information_agent.details.jade.utils.*;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;

public class SendMessageAgent extends Agent implements SimpleAgentInterface {

    private static final String TAG = "SendMessageAgent";
    private static final long serialVersionUID = 1594371294421614291L; // ubique id
    private Set participants = new SortedSetImpl(); // using jade android leap'S method
    private Codec codec = new SLCodec();
    private Context context;
    private String ipAddress = "127.0.0.1";// localhost server
    private String agentName = "android-agent";

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof Context) {
                context = (Context) args[0];
            }
        }

        // Add initial behaviours
        addBehaviour(new SendMessage(this, 3000));

        // Activate the GUI
        registerO2AInterface(SimpleAgentInterface.class, this);

        Intent broadcast = new Intent();
        broadcast.setAction("jade.demo.agent.SEND_MESSAGE");
        Log.i(TAG, "###Sending broadcast " + broadcast.getAction());
        context.sendBroadcast(broadcast);

        //Get ipAddress and agentName
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.PREFS_HOST_ADDRESS, ipAddress);
        agentName = sharedPreferences.getString(Constants.PREFS_AGENT_NAME, agentName);
    }

    protected void takeDown() {
    }

    class SendMessage extends TickerBehaviour {

        public SendMessage(Agent a, long period) {
            super(a, period);
        }

        @Override
       protected void onTick() {
            Log.i(TAG, "###on Tick");
            sendDummyMessage();
        }

        private void sendDummyMessage() {
            ACLMessage message = new ACLMessage(ACLMessage.CONFIRM);
            message.setLanguage(codec.getName());
            String convId = "C-" + myAgent.getLocalName();
            message.setConversationId(convId);
            message.setContent("hello! I am from android mobile [Sending message Agent]");
            AID dummyAid = new AID();
            dummyAid.setName(agentName + "@" + ipAddress + ":1099/JADE");
           dummyAid.addAddresses("http://"+ipAddress+"7778/acc");

//            dummyAid.

            message.addReceiver(dummyAid);
            myAgent.send(message);
            Log.i(TAG, "###Send message:" + message.getContent());
            exportLog("Send message:" + message.getContent());
        }

    }



    public String[] getParticipantNames() {
        String[] participantnames = new String[participants.size()];
        return participantnames;
    }

    public void onHostChanged(String host) {
        ipAddress = host;
    } // assigning ip address when host changes

    public void onAgentNameChanged(String name) {
        agentName = name;
    }
    // assigning new name when host changes

    private void exportLog(String log) {
        MainActivity mainActivity = (MainActivity)context;
        mainActivity.exportLogConsole(log);
    }

    // keepping a check thrut the log file
}
