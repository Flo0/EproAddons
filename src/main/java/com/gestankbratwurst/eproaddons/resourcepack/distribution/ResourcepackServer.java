package com.gestankbratwurst.eproaddons.resourcepack.distribution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public class ResourcepackServer extends Thread {

  private volatile boolean running = true;

  protected final int port;
  protected final ServerSocket socket;

  public ResourcepackServer(final int port) throws IOException {
    this.port = port;
    this.socket = new ServerSocket(port);
    this.socket.setReuseAddress(true);
  }

  @Override
  public void run() {
    while (this.running) {
      try {
        new Thread(new ResourceServerConnection(this, this.socket.accept())).start();
      } catch (final IOException e) {
        Bukkit.getLogger().warning("A thread was interrupted in the http daemon!");
      }
    }
    if (!this.socket.isClosed()) {
      try {
        this.socket.close();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void terminate() {
    this.running = false;
    if (!this.socket.isClosed()) {
      try {
        this.socket.close();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
  }

  public File requestFileCallback(final ResourceServerConnection connection, final String request) {
    return null;
  }

  public void onSuccessfulRequest(final ResourceServerConnection connection, final String request) {
  }

  public void onClientRequest(final ResourceServerConnection connection, final String request) {
  }

  public void onRequestError(final ResourceServerConnection connection, final int code) {
  }

  public class ResourceServerConnection implements Runnable {

    protected final ResourcepackServer server;
    protected final Socket client;

    public ResourceServerConnection(final ResourcepackServer server, final Socket client) {
      this.server = server;
      this.client = client;
    }

    public Socket getClient() {
      return this.client;
    }

    @Override
    public void run() {
      try {
        final BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream(), "8859_1"));
        final OutputStream out = this.client.getOutputStream();
        final PrintWriter pout = new PrintWriter(new OutputStreamWriter(out, "8859_1"), true);
        String request = in.readLine();
        ResourcepackServer.this.onClientRequest(this, request);

        final Matcher get = Pattern.compile("GET /?(\\S*).*").matcher(request);
        if (get.matches()) {
          request = get.group(1);
          final File result = ResourcepackServer.this.requestFileCallback(this, request);
          if (result == null) {
            pout.println("HTTP/1.0 400 Bad Request");
            ResourcepackServer.this.onRequestError(this, 400);
          } else {
            try (final FileInputStream fis = new FileInputStream(result)) {
              // Writes zip files specifically;
              out.write("HTTP/1.0 200 OK\r\n".getBytes());
              out.write("Content-Type: application/zip\r\n".getBytes());
              out.write(("Content-Length: " + result.length() + "\r\n").getBytes());
              out.write(("Date: " + new Date().toInstant() + "\r\n").getBytes());
              out.write("Server: Httpd\r\n\r\n".getBytes());
              final byte[] data = new byte[64 * 1024];
              for (int read; (read = fis.read(data)) > -1; ) {
                out.write(data, 0, read);
              }
              out.flush();
              ResourcepackServer.this.onSuccessfulRequest(this, request);
            } catch (final FileNotFoundException e) {
              pout.println("HTTP/1.0 404 Object Not Found");
              ResourcepackServer.this.onRequestError(this, 404);
            }
          }
        } else {
          pout.println("HTTP/1.0 400 Bad Request");
          ResourcepackServer.this.onRequestError(this, 400);
        }
        this.client.close();
      } catch (final IOException e) {
        System.out.println("Oh no, it's broken D: " + e);
      }
    }
  }
}