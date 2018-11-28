package com.ouc.tcp.test;

import java.util.TimerTask;

import com.ouc.tcp.client.Client;
import com.ouc.tcp.message.TCP_PACKET;

/*******************
 * 
 * @author gray
 * improve from TransWindow : use packets directly
 */
public class TaskPacketsRetrans extends TimerTask {
	
	private Client senderClient;
	private TCP_PACKET[] packets;
	
	
	/*??????*/
	public TaskPacketsRetrans(Client client, TCP_PACKET[] packets4Retrans) {
		super();
		senderClient = client;		
		packets = packets4Retrans;
	}	

	
	@Override
	/*???TCP?????*/
	public void run() {
		
		for (int i=0;i<packets.length;i++)
			senderClient.send(packets[i]);		
	}
}
