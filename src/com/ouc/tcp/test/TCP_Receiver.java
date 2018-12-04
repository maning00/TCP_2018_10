package com.ouc.tcp.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ouc.tcp.client.TCP_Receiver_ADT;
import com.ouc.tcp.message.*;
import com.ouc.tcp.tool.TCP_TOOL;

public class TCP_Receiver extends TCP_Receiver_ADT {

	int sequence=1;//用于记录当前待接收的包序号
	private TCP_PACKET ackPack;	//回复的ACK报文段

	/*构造函数*/
	public TCP_Receiver() {
		super();	//调用超类构造函数
		super.initTCP_Receiver(this);	//初始化TCP接收端
	}

	@Override
	//接收到数据报：检查校验和，设置回复的ACK报文段
	public void rdt_recv(TCP_PACKET recvPack) {
		//检查校验码，生成ACK
		if(sequence==recvPack.getTcpH().getTh_seq()){     //待接受与收到的相同，直接收下即可。
		tcpH.setTh_ack(recvPack.getTcpH().getTh_seq());
		ackPack = new TCP_PACKET(tcpH, tcpS, recvPack.getSourceAddr());
		if(CheckSum.computeChkSum(recvPack) == recvPack.getTcpH().getTh_sum()) {

			//生成ACK报文段（设置确认号）

			tcpH.setTh_sum(CheckSum.computeChkSum(ackPack));
			ackPack.setTcpH(tcpH);

			//回复ACK报文段
			reply(ackPack);

			//有重复数据的情况下需要检查数据顺序号（确定是否接收了重复的数据）
			//去除报文中的顺序号
			int seq = recvPack.getTcpH().getTh_seq();


			//判断是否是重复数据：非重复数据，将数据插入data队列
			int[] data = recvPack.getTcpS().getData();
			dataQueue.add(data);
			//更新期待接收的顺序号
			if(sequence==1){
				sequence=0;
			} else if(sequence==0){
				sequence=1;
			}

		}

		// 校验出错要求重发
		if (CheckSum.computeChkSum(recvPack) != recvPack.getTcpH().getTh_sum()) {
			tcpH.setTh_ack(recvPack.getTcpH().getTh_seq() - recvPack.getTcpS().getData().length);
			tcpH.setTh_sum(CheckSum.computeChkSum(ackPack));
			ackPack.setTcpH(tcpH);
			reply(ackPack);
			return;
		}

		//向上层应用——写文件，交付数据
		if(dataQueue.size() >= 20)
			deliver_data();
	} else if(sequence!=recvPack.getTcpH().getTh_seq()){        //待接受与收到的不同，说明接收方重发，上个ack包没收到，故再发一次上个ack
			int tmp=0;
			if(sequence==1){
				tmp=0;
			}else if (sequence==0){
				tmp=1;
			}
			tcpH.setTh_ack(tmp);
			ackPack = new TCP_PACKET(tcpH, tcpS, recvPack.getSourceAddr());
			tcpH.setTh_sum(CheckSum.computeChkSum(ackPack));
			ackPack.setTcpH(tcpH);
			reply(ackPack);
		}
	}

	@Override
	//交付数据（将数据写入文件）
	public void deliver_data() {
		//检查dataQueue，将数据写入文件
		File fw = new File("recvData.txt");
		BufferedWriter writer;

		try {
			writer = new BufferedWriter(new FileWriter(fw, true));

			//循环检查data队列中是否有新交付数据
			while(!dataQueue.isEmpty()) {
				int[] data = dataQueue.poll();

				//将数据写入文件
				for(int i = 0; i < data.length; i++) {
					writer.write(data[i] + "\n");
				}

				writer.flush();		//清空输出缓存
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	//回复ACK报文段
	public void reply(TCP_PACKET replyPack) {
		//设置错误控制标志
		tcpH.setTh_eflag(EFlagValue.eflag);	//eFlag=0，信道无错误

		//发送数据报
		client.send(replyPack);

	}

}
