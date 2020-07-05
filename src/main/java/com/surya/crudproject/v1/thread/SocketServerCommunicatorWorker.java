package com.surya.crudproject.v1.thread;

import com.surya.crudproject.common.exception.CrudProjectException;
import com.surya.crudproject.common.model.CrudProjectStatusCode;
import com.surya.crudproject.v1.util.SocketListener;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;


@Slf4j
public class SocketServerCommunicatorWorker extends Thread {

  private Queue<String> notificationQueue;
  private DataOutputStream dout = null;

  public SocketServerCommunicatorWorker(Queue<String> notificationQueue) {
    this.notificationQueue = notificationQueue;
  }

  public void run() {
      this.initializeSocketConnection();
      while (true) {
        if (!notificationQueue.isEmpty()) {
          this.writeToClient(notificationQueue.poll());
        }
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

  private void initializeSocketConnection() {
    try {
      ServerSocket ss = new ServerSocket(6666);
      //runSocketListener();
      Socket s = ss.accept();
      dout = new DataOutputStream(s.getOutputStream());
    } catch (IOException e) {
      log.error("IOException occurred while writing to client");
      throw new CrudProjectException(CrudProjectStatusCode.PROCESSING_ERROR);
    }
  }
}
