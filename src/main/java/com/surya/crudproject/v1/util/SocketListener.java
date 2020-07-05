package com.surya.crudproject.v1.util;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

// A Java program for a Client

@Slf4j
public class SocketListener {

  private DataInputStream dis = null;

  // constructor to put ip address and port
  public SocketListener(String address, int port) {
    Socket socket = null;
    try {
      socket = new Socket(address, port);
      dis = new DataInputStream(socket.getInputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void readSocket() {
    while (true) {
      String str = null;
      try {
        str = (String) dis.readUTF();
        //log.info("message => {}" , str);
      } catch (IOException e) {
        //e.printStackTrace();
      }
    }
  }
}
