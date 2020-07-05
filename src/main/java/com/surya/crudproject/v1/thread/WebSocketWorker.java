package com.surya.crudproject.v1.thread;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.surya.crudproject.common.constant.CommonConstant;
import com.surya.crudproject.v1.model.BarModel;
import com.surya.crudproject.v1.util.SocketServerUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Queue;

@Slf4j
public class WebSocketWorker extends Thread {

  private Queue<BarModel> eventQueue;
  private SocketServerUtil socketServerUtil;

  public WebSocketWorker(Queue<BarModel> eventQueue) throws IOException {
    this.eventQueue = eventQueue;
    this.socketServerUtil = new SocketServerUtil(6666);

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
      socketServerUtil.writeToClient(message);
      //log.info("{}",barModel);
    } catch (JsonProcessingException e) {
      log.error("Exception occurred ", e);
    }
  }
}
