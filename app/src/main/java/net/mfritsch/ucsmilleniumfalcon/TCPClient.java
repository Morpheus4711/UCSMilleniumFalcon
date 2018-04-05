package net.mfritsch.ucsmilleniumfalcon;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

class TCPClient implements Runnable {
    public PrintWriter dataOut;
    public BufferedReader dataIn;
    String TAG = TCPClient.class.getSimpleName();
    private String dstAddress;
    private int dstPort;
    private String jsonCommand;

    TCPClient(String dstAddress, int dstPort, String jsonCommand) {
        this.dstAddress = dstAddress;
        this.dstPort = dstPort;
        this.jsonCommand = jsonCommand;
    }

    public void run() {

        //int p = Integer.parseInt(dstPort.getText().toString());
        // int p = dstPort;
        //String h = hostname.getText().toString();
        // String h = dstAddress;

        mkmsg("Host is " + dstAddress + "\n");
        Log.v(TAG, "Host is " + dstAddress);

        mkmsg("Port is " + dstPort + "\n");
        Log.v(TAG, "Port is " + dstPort);
        try {
            InetAddress serverAddr = InetAddress.getByName(dstAddress);

            //mkmsg("Attempt Connecting..." + dstAddress + "\n");
            //Log.v(TAG, "Attempt Connecting..." + dstAddress);

            Socket socket = new Socket(serverAddr, dstPort);
            mkmsg("Attempt Connecting..." + socket);
            Log.v(TAG, "Attempt Connecting..." + socket);

            //String message = "Hello from Client android emulator";

            //made connection, setup the read (in) and write (out)
            dataOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            dataIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //now send a message to the server and then read back the response.
            try {
                //write a message to the server
                mkmsg("Attempting to send message ...\n");
                Log.v(TAG, "Attempting to send message ...");
                dataOut.println(jsonCommand);
                mkmsg("Message sent...\n");
                Log.v(TAG, "Message sent: " + jsonCommand);

                //read back a message from the server.
                mkmsg("Attempting to receive a message ...\n");
                Log.v(TAG, "Attempting to receive a message ...");

                String str = dataIn.readLine();
                mkmsg("received a message:\n" + str + "\n");
                Log.v(TAG, "received a message: " + str);

                mkmsg("We are done, closing connection\n");
                Log.v(TAG, "We are done, closing connection");
            } catch (UnknownHostException e) {
                e.printStackTrace();
                mkmsg("UnknownHostException: " + e.toString() + "\n");
                Log.i(TAG, "UnknownHostException: " + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                mkmsg("IOException:\n" + e.toString() + "\n");
                Log.i(TAG, "IOException: " + e.toString());
            } catch (Exception e) {
                mkmsg("Error happened sending/receiving\n");
                Log.v(TAG, "Error happened sending/receiving");

            } finally {
                // close input stream
                if (dataIn != null) {
                    try {
                        dataIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.v(TAG, "Error happened dataIn closing " + e.toString());
                    }
                }

                // close output stream
                if (dataOut != null) {
                    try {
                        dataOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v(TAG, "Error happened dataOut closing " + e.toString());
                    }
                }
                // close socket
                if (socket != null) {
                    try {
                        Log.i(TAG, "closing the socket");
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.v(TAG, "Error happened closing the socket" + e.toString());
                    }
                }
            }

        } catch (Exception e) {
            mkmsg("Unable to connect...\n");
            Log.v(TAG, "Unable to connect...");

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // output.append(msg.getData().getString("msg"));
        }

    };

    public void mkmsg(String str) {
        //handler junk, because thread can't update screen!
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("msg", str);
        msg.setData(b);
        handler.sendMessage(msg);
    }
}