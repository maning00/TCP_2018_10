package com.ouc.tcp.test;

import com.ouc.tcp.client.TCP_Sender_ADT;
import com.ouc.tcp.client.UDT_RetransTask;
import com.ouc.tcp.client.UDT_Timer;
import com.ouc.tcp.message.*;
import com.ouc.tcp.tool.TCP_TOOL;

public class TCP_Sender extends TCP_Sender_ADT {
	
	private TCP_PACKET tcpPack;	//�����͵�TCP���ݱ�
	private UDT_Timer timer;	//��������ʱ��

	
	/*���캯��*/
	public TCP_Sender() {
		super();	//���ó��๹�캯��
		super.initTCP_Sender(this);		//��ʼ��TCP���Ͷ�
	}
	
	@Override
	//�ɿ����ͣ�Ӧ�ò���ã�����װӦ�ò����ݣ�����TCP���ݱ�
	public void rdt_send(int dataIndex, int[] appData) {
		//����TCP���ݱ���������ź������ֶ�/У���),ע������˳��
		tcpH.setTh_seq(dataIndex * appData.length + 1);//���������Ϊ�ֽ����ţ���Ҳ����ʹ��������ŷ�ʽ��ע���޸Ķ�Ӧ�Ľ��շ��ж���ŵĲ���
		tcpH.setTh_sum((short)0);//�Ƚ�У������Ϊ0�����ں����ļ���
		tcpS.setData(appData);		
		tcpPack = new TCP_PACKET(tcpH, tcpS, destinAddr);		
		//����У���룻��Ҫ���½�tcpH���뵽tcpPack				
		tcpH.setTh_sum(CheckSum.computeChkSum(tcpPack));
		tcpPack.setTcpH(tcpH);
		System.out.println("****" + tcpH.getTh_seq() + "****");
		//����TCP���ݱ�
		udt_send(tcpPack);
		
		/**************************/
		/**��ʱ�����÷�����ʱ����ʱ������ش�����Ҫ��UDT_Timer���ڼ�ʱ����ʱ��0�󣬴���UDT_RetransTask����ش�**/
		//timer = new UDT_Timer();
		/**�ش���UDT_RetransTask�����Ͷ˺ͷ���������Ϊ��Ա����**/
		//UDT_RetransTask reTrans = new UDT_RetransTask(client, tcpPack);
		/**UDT_Timer��ʼ��ʱ��һ���ش�Ϊ5s���Ժ�ÿ���3s���һ���ش���������ֶԷ����ճɹ�����Ҫ��waitACK()�йرռ�ʱ��**/
		//timer.schedule(reTrans, 5000, 5000);
		
		//��waitACKʹ������ѭ����Break����ʵ��ֹͣ�ȴ������漰Go-Back-N �� Selective-Response�Ļ����Ͳ�������ֹͣ�ȴ���
		waitACK();
	
	}
	
	@Override
	//���ɿ����ͣ�������õ�TCP���ݱ�ͨ�����ɿ������ŵ�����
	public void udt_send(TCP_PACKET tcpPack) {
		//���ô�����Ʊ�־
		tcpH.setTh_eflag(EFlagValue.eflag);
		
		//����У��ͣ�����TCP�ײ����´��
		
		//�������ݱ�
		client.send(tcpPack);
	}
	
	@Override
	//����ACK���ģ�������ACK�봦��ACK�ֿ�
	public void waitACK() {
		//ѭ�����ackQueue;
		//ʹ������ѭ����Break����ʵ��ֹͣ�ȴ������漰Go-Back-N �� Selective-Response�Ļ����Ͳ�������ֹͣ�ȴ���
		while (true) {
			/*if(!ackQueue.isEmpty() && ackQueue.poll() == tcpPack.getTcpH().getTh_seq()) {
				/**RDT3.0ֹͣ�ȴ���ʱ����Ҫ�رռ�ʱ��**/
			//timer.cancel();*/
			//break;
			if (!ackQueue.isEmpty()) {
				Integer ack = ackQueue.poll();
				if (ack == tcpPack.getTcpH().getTh_seq()) {
					break;
				} else if (ack != tcpPack.getTcpH().getTh_seq()) {
					System.out.println(tcpPack);
					udt_send(tcpPack);
					waitACK();
					break;

				}
			}

		}
	}

	@Override
	//���յ�ACK���ģ����У��ͣ���ȷ�ϺŲ���ack����
	public void recv(TCP_PACKET recvPack) {
		//��Ҫ���У���
		
		//��ӡACK�ţ����ڵ���
		System.out.println("Receive ACK Number�� "+ recvPack.getTcpH().getTh_ack());
		//��ACK�Ų�����еȴ���WaitACK��������������ջظ��ֿ�
		ackQueue.add(recvPack.getTcpH().getTh_ack());
		
	}
	
}
