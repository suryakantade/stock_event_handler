package com.surya.crudproject.v1.thread;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.surya.crudproject.common.constant.CommonConstant;
import com.surya.crudproject.v1.model.BarModel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class WebSocketWorker extends Thread {


  private Queue<BarModel> eventQueue;
  private Queue<String> socketNotificationQueue;


  public WebSocketWorker(Queue<BarModel> eventQueue) throws IOException {
    this.eventQueue = eventQueue;
    socketNotificationQueue = new LinkedBlockingDeque<>();
    SocketServerCommunicatorWorker socketServerCommunicatorWorker =
        new SocketServerCommunicatorWorker(socketNotificationQueue);
    socketServerCommunicatorWorker.start();
  }

  @Override
  public void run() {
    while (true) {
      if (!eventQueue.isEmpty()) {
        sendMessage(eventQueue.poll());
      }
    }
  }

  private void sendMessage(BarModel barModel) {
    String message = null;
    try {
      message = CommonConstant.OBJECT_MAPPER.writeValueAsString(barModel);
      this.socketNotificationQueue.add(message);
      log.info("{}", barModel);
    } catch (JsonProcessingException e) {
      log.error("Exception occurred ", e);
    }
  }
}
