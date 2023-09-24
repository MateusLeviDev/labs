package demo.com.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestUtil {
    public static InetAddress PUBLIC_IP;

    /**
     * List all IP addresses of the device, except loopback addresses.
     *
     * @return a List of IP address
     */
    public static List<InetAddress> getIPAddresses() throws IOException {
        List<InetAddress> addresses = new ArrayList<InetAddress>();
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface intf : interfaces) {
            List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
            for (InetAddress a : addrs) {
                if (a.isLoopbackAddress()) {
                    continue;
                }
                addresses.add(a);
            }
        }
        return addresses;
    }

    public static String getIP() throws SocketException, MalformedURLException {
        URL ip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    ip.openStream()));
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
