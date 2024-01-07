package Packet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.*;


import java.lang.reflect.Type;
import java.util.Collection;



// TODO: Add setters.
public class Packet {
    private String source;        // Sender's ID.
    private String destination;  // Receiver's ID.
    private String playerID;    // Player's ID.
    private byte flag;          // Packet's flag.
    private byte protocol;      // Packet's protocol.
    private boolean approved;   // Server's approval. Even if clients modify it, it will be ignored.
    private boolean nameSet;    // If playerID has been set.
    private String data;        // Packet's data.

    static private Gson gson = new Gson(); // Serialiser. I might remove it, not sure.

    public Packet(){playerID = ""; protocol = (byte) -1; approved = false; data = ""; nameSet = false;}

    public void setPlayerID(Packet packet){
        if (!nameSet) {
            playerID = (String) packet.extractData(String.class);
            System.out.println("Player ID set to: " + playerID);
            nameSet = true;
        }
    }

    // TODO: different protocol generations. Client should be unable to place anything in approval.
    // TODO: Could add field that checks if server or client.
    public String generatePacket(byte protocol, boolean approval, Object data){
        // TODO: Add checks.
        this.protocol = protocol;
        approved = approval;
        // Serialise data, if there is any.
        if (data != null) this.data = gson.toJson(data);
        return gson.toJson(this);
    }

    public String generatePacket(byte protocol, byte flag, boolean approval, Object data){
        // TODO: Add checks.
        this.protocol = protocol;
        this.approved = approval;
        this.flag = flag;
        // Serialise data, if there is any.
        if (data != null) this.data = gson.toJson(data);
        return gson.toJson(this);
    }

    public void copyPacket(Packet packet){
        this.protocol = packet.protocol;
        this.approved = packet.approved;
        this.flag = packet.flag;
        this.data = packet.data;
    }

    public boolean assignedID(){return playerID.equals("");}
    public byte getProtocol() {return protocol;}
    public String getID() {return playerID;}
    public byte getFlag() {return flag;}
    public boolean approved() {return approved;}
    public <T> T extractData(Class<?> type){return (T) gson.fromJson(data, type);}
    public <T> T extractData(Type type){
        return gson.fromJson(data, type);
    }
    static public Packet convertMessageToPacket(String json){return gson.fromJson(json, Packet.class);}
    private boolean invalidProtocol(){return protocol < 0 || protocol > 9;}
    public boolean hasName(){return !playerID.equals("");}
}
