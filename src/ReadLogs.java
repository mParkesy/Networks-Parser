
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author xze15agu
 */
public class ReadLogs {

    
    
    public static ArrayList<String> getFileList(String file) {
        ArrayList<String> list = new ArrayList<>();
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                list.add(strLine);
            }
            fstream.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return list;
    }

    public static ArrayList<Host> getHostList() {
        ArrayList<Host> hosts = new ArrayList<>();
        hosts.add(new Host("PLACEHOLDER", "45.221.77.2"));
        hosts.add(new Host("PLACEHOLDER", "116.90.239.10"));
        hosts.add(new Host("PLACEHOLDER", "41.72.99.143"));
        hosts.add(new Host("PLACEHOLDER", "163.43.24.70"));
        hosts.add(new Host("PLACEHOLDER", "133.130.35.170"));
        hosts.add(new Host("PLACEHOLDER", "103.242.31.11"));
        hosts.add(new Host("Malaysia FTP (don't use)", "103.75.188.168"));
        hosts.add(new Host("PLACEHOLDER", "107.190.134.163"));
        hosts.add(new Host("PLACEHOLDER", "103.241.150.190"));
        hosts.add(new Host("PLACEHOLDER", "112.137.166.236"));
        hosts.add(new Host("France", "178.33.235.187"));
        hosts.add(new Host("Singapore", "101.100.209.220"));
        hosts.add(new Host("PLACEHOLDER", "116.12.51.45"));
        hosts.add(new Host("PLACEHOLDER", "188.42.162.69"));
        hosts.add(new Host("PLACEHOLDER", "116.12.51.45"));
        hosts.add(new Host("PLACEHOLDER", "180.92.193.10"));
        hosts.add(new Host("PLACEHOLDER", "104.20.72.23"));
        hosts.add(new Host("PLACEHOLDER", "202.78.202.70"));
        hosts.add(new Host("PLACEHOLDER", "110.4.45.6"));
        hosts.add(new Host("PLACEHOLDER", "200.170.94.6"));
        return hosts;
    }

    public static ArrayList<PingBlock> getPingList(ArrayList<Host> hosts, String file) throws IOException {
        ArrayList<String> list = getFileList(file);

        PingBlock ping = new PingBlock();
        ArrayList<PingBlock> blockList = new ArrayList<>();
        int count = 0;
        FileWriter writer = new FileWriter("pingTest.csv");
        StringBuilder sb = new StringBuilder();
        Host h = null;
        String columns = "Date, Host, Packet loss, min, avg, max, mdev, type";
        sb.append(columns);
        sb.append("\n");
        writer.append(sb.toString());

        for (int i = 0; i < list.size(); i++) {
            String current = list.get(i);

            if (current.startsWith("PING")) {
                h = hosts.get(count);
                count++;
            }
            if (current.contains("BST")) {
                sb = new StringBuilder();
                current = current.replace("BST", "");
                sb.append(current);
                sb.append(", ");
                ping.setTimestamp(current);
            } else if (current.startsWith("-")) {
                String packetLossLine = list.get(i + 1);
                packetLossLine = h.getIP() + ", " + packetLossLine;
                String[] blocks = packetLossLine.split(",");
                String packetLoss = blocks[3];
                packetLoss = packetLoss.replaceAll(" packet loss", "");
                packetLoss = packetLoss.replaceAll("%", "");
                ping.setPacketLoss(packetLoss);
                h = hosts.get(count);
                ping.setHost(h);
                // ip
                sb.append(blocks[0]);
                sb.append(", ");
                // packet loss
                sb.append(packetLoss);
                sb.append(", ");

            } else if (current.startsWith("rtt")) {

                String rttData = list.get(i);
                rttData = rttData.replaceFirst("rtt min/avg/max/mdev = ", "");
                rttData = rttData.replaceAll(" ms", "");
                String[] block = rttData.split("/");
                ping.setMin(Double.parseDouble(block[0]));
                ping.setAvg(Double.parseDouble(block[1]));
                ping.setMax(Double.parseDouble(block[2]));
                ping.setMdev(Double.parseDouble(block[3]));
                blockList.add(ping);

                rttData = rttData.replaceAll("/", ",");
                sb.append(rttData);
                sb.append(", ");
                sb.append(ping.getType());
                sb.append("\n");

                writer.append(sb.toString());

            }
            if (count == 20) {
                count = 0;
            }
        }
        writer.close();
        return blockList;
    }

    public static void main(String[] args) throws IOException {
        String file = "ping.log";

        ArrayList<Host> hosts = getHostList();

        ArrayList<PingBlock> pingList = getPingList(hosts, file);

        file = "trace.log";
        ArrayList<String> list = getFileList(file);
        

    }
}
