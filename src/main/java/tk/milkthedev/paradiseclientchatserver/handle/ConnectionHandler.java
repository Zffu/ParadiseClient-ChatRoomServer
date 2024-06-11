package tk.milkthedev.paradiseclientchatserver.handle;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import static tk.milkthedev.paradiseclientchatserver.ServerImpl.getServer;

public class ConnectionHandler implements Runnable
{
    private final Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    public ConnectionHandler(Socket client)
    {
        this.client = client;
    }

    @Override
    public void run()
    {
        try
        {
            in = new BufferedReader(new java.io.InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            username = in.readLine();
            out.println("You are now connected to the server!");
            getServer().broadcast(username + " joined the chat!");

            String message;
            try
            {
                while ((message = in.readLine()) != null)
                {
                    if (message.startsWith("[COMMAND]~~~"))
                    {
                        String command = message.split("~~~")[1];
                        if (command.equals("quit"))
                        {
                            getServer().broadcast(username + " left the chat!");
                            client.close();
                            return;
                        } else {out.println("Unknown command!");}
                    }
                    getServer().broadcast(username + ": " + message);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                getServer().broadcast(username + " left the chat!");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message)
    {
        out.println(message);
    }

    public boolean closeConnection(String message)
    {
        try
        {
            sendMessage(message);
            client.close();
            getServer().broadcast(username + " left the chat!");
            return true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}