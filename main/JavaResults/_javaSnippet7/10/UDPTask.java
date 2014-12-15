package com.smw.skylight ;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.AsyncTask;
import android.util.Log;

public class UDPTask extends AsyncTask<String, Void, String> {

    private Exception exception;

@Override
protected String doInBackground(String... params) {
 
    String message =  params[0];
    String udp_ip  =  params[1];
    String[] udp_ip_parts = udp_ip.split(":");
    String ip   = udp_ip_parts[0];
    int socket = Integer.parseInt(udp_ip_parts[1]);
    
    Log.d(">> UDP TASK"  , "Sending "+message+" to "+ip+" : "+socket);
	DatagramSocket ds = null;
    InetAddress local = null;
    
    try 
    {
    	local = InetAddress.getByName(ip);
    	//local = InetAddress.getByName("192.168.1.42");
    	//local = InetAddress.getByName("64.106.96.137");
    	ds = new DatagramSocket();
        DatagramPacket dp;                          
        dp = new DatagramPacket(message.getBytes(), message.length(), local, socket);
        ds.setBroadcast(true);
        ds.send(dp);
    } 
    catch (Exception e) 
    {
        e.printStackTrace();
    }
    finally 
    {
        if (ds != null) 
        {   
            ds.close();
        }
    }
    return null; 
}  
    


//    protected RSSFeed doInBackground(String... urls) {
//        try {
//            URL url= new URL(urls[0]);
//            SAXParserFactory factory =SAXParserFactory.newInstance();
//            SAXParser parser=factory.newSAXParser();
//            XMLReader xmlreader=parser.getXMLReader();
//            RssHandler theRSSHandler=new RssHandler();
//            xmlreader.setContentHandler(theRSSHandler);
//            InputSource is=new InputSource(url.openStream());
//            xmlreader.parse(is);
//            return theRSSHandler.getFeed();
//        } catch (Exception e) {
//            this.exception = e;
//            return null;
//        }
//    }

    protected void onPostExecute(String message) {
        // TODO: check this.exception 
        // TODO: do something with the feed
    }




}