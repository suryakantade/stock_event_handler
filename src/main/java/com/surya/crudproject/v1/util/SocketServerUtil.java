package com.surya.crudproject.v1.util;

import com.surya.crudproject.common.exception.CrudProjectException;
import com.surya.crudproject.common.model.CrudProjectStatusCode;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


@Slf4j
public class SocketServerUtil {

  private DataOutputStream dout = null;

  public SocketServerUtil(int port) {
    try {
      ServerSocket ss = new ServerSocket(port);
      //runSocketListener();
      Socket s = ss.accept();
      dout = new DataOutputStream(s.getOutputStream());
    } catch (IOException e) {
      log.error("IOException occurred while writing to client");
      throw new CrudProjectException(CrudProjectStatusCode.PROCESSING_ERROR);
    }
  }

  public void writeToClient(String message) {
    log.debug("writing to client through the socket");
    try {
      dout.writeUTF(message);
      dout.flush();
    } catch (IOException e) {
      log.error("IOException occurred while writing to client");
      throw new CrudProjectException(CrudProjectStatusCode.PROCESSING_ERROR);
    }
  }

  private void runSocketListener() {
    new Thread(new Runnable() {
      public void run() {
        SocketListener socketListener = new SocketListener("localhost", 6666);
        socketListener.readSocket();
      }
    }).start();
  }
}
