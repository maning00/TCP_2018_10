package com.ouc.tcp.test;
import com.ouc.tcp.message.TCP_HEADER;
import com.ouc.tcp.message.TCP_PACKET;
public class CheckSum {
    private static int NO = 0;
    /* 计算TCP报文段校验和：只需校验TCP首部中的seq、ack和sum，以及TCP数据字段 */
    public static short computeChkSum(TCP_PACKET tcpPack) {
        int checkSum = 0;
        // 计算校验和
        TCP_HEADER tcpH = tcpPack.getTcpH();
        int[] data = tcpPack.getTcpS().getData();
        //
        int ack = tcpH.getTh_ack();
        int seq = tcpH.getTh_seq();
        int sum = tcpH.getTh_sum();
        ack = ~ack;
        seq = ~seq;
        sum = ~sum;
        ack = add(ack, 0);
        seq = add(seq, 0);
        sum = add(sum, 0);
        checkSum = add(checkSum, seq);
        checkSum = add(checkSum, sum);
        checkSum = add(checkSum, ack);
        System.out.println("----------------------------");
        System.out.println("ack :" + ack + "  ~ack :" + (~ack));
        System.out.println("seq :" + seq + "  ~seq :" + (~seq));
        System.out.println("sum :" + sum + "  ~sum :" + (~sum));
        System.out.println("----------------------------");
        // System.out.println("checkSum = (~ack) + (~seq) + (~sum) :" + checkSum);
        // System.out.println("checkSum = (~ack) + (~seq) + (~sum)(hex) :"+
        // Integer.toHexString(checkSum));
        // System.out.println("**********");
        for (int d : data) {
            int c = ~d;
            c = add(c, 0);
            checkSum = add(checkSum, c);
            // System.out.print(~d + " ");
        }
        /*
         * System.out.println(); System.out.println("**********");
         * System.out.println("checkSum2 += data[] :" + checkSum2);
         * System.out.println("checkSum2 += data[](hex) :" + Integer.toHexString(checkSum2));
         * System.out.println("checkSum(hex) : " + Integer.toHexString(checkSum));
         */
        // 加两次防止第一次加完有进位
        /*
         * System.out.println("final checkSum : " + checkSum);
         * System.out.println("final checkSum(hex) : " + Integer.toHexString(checkSum));
         * System.out.println("----------------------------"); System.out.println("NO : " + NO++);
         * System.out.println();
         */
        return (short) checkSum;
    }
    private static int add(int s1, int s2) {
        s1 = sum16Bit(s1);
        s1 = sum16Bit(s1);
        s2 = sum16Bit(s2);
        s2 = sum16Bit(s2);
        s1 += s2;
        s1 = sum16Bit(s1);
        s1 = sum16Bit(s1);
        return s1;
    }
    private static int sum16Bit(int source) {
        short low16 = (short) ((source << 16) >> 16);
        short top16 = (short) (source >> 16);
        low16 = calibrate(low16);
        top16 = calibrate(top16);
        /*
         * System.out.println("!!!!!!!!!!!!!"); System.out.println("source hex : " +
         * Integer.toHexString(source)); System.out.println("low hex : " +
         * Integer.toHexString(low16)); System.out.println("low : " + (short) low16);
         * System.out.println("top hex : " + Integer.toHexString(top16));
         * System.out.println("top : " + top16); System.out.println("low final : " + low16);
         * System.out.println("top final : " + top16);
         *
         * System.out.println("!!!!!!!!!!!!!");
         */
        int sum = low16 + top16;
        return sum;
    }
    private static short calibrate(short num) {
        return num == (short) 0xFFFF || num == (short) 0 ? (short) 0 : num;
    }
}