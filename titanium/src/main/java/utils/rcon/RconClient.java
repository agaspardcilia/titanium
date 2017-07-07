package utils.rcon;


import java.io.*;
import java.net.Socket;
import java.util.Random;

public class RconClient implements AutoCloseable {
    private Random rng = new Random();
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    private final String host;
    private final int port;

    public RconClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public boolean authenticate(String password) throws IOException, IllegalStateException {
        if (socket == null || !socket.isBound())
            throw new IllegalStateException("Not connected.");

        int packetId = rng.nextInt(Integer.MAX_VALUE);
        write(new Packet(packetId, Packet.Type.SERVERDATA_AUTH, password));

        read(); // Ignore the first empty packet
        Packet response = read();
        return response.getPacketType() == Packet.Type.SERVERDATA_AUTH_RESPONSE && response.getPacketId() == packetId;
    }

    public void executeCommand(String command) throws IOException {
        int commandPacketId = rng.nextInt(Integer.MAX_VALUE);

        write(new Packet(commandPacketId, Packet.Type.SERVERDATA_EXECCOMMAND, command));
    }

    public Packet read() throws IOException {
        Packet packet = new Packet();
        packet.read(in);
        if (packet.getPacketType() == null)
            return read();
        return packet;
    }

    public void write(Packet packet) throws IOException {
        packet.write(out);
        out.flush();
    }

    public boolean connected() {
        return socket != null && socket.isBound() && !socket.isClosed();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
