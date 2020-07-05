package com.surya.crudproject.v1.thread;

import com.surya.crudproject.common.constant.CommonConstant;
import com.surya.crudproject.common.exception.CrudProjectException;
import com.surya.crudproject.v1.model.BarModel;
import com.surya.crudproject.v1.model.SubscriptionConfig;
import com.surya.crudproject.v1.model.TradeModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;


@Slf4j
public class FileReaderWorker implements Callable<Boolean> {

  private File file;

  private List<SubscriptionConfig> subscriptionConfigs;

  private Map<String, List<Queue<TradeModel>>> tradeModelQueueHolder;


  public FileReaderWorker(File file, List<SubscriptionConfig> subscriptionConfigs) {
    this.file = file;
    this.subscriptionConfigs = subscriptionConfigs;
    tradeModelQueueHolder = new HashMap<>();
  }

  @Override
  public Boolean call() throws Exception {
    log.debug("reading file and feeding events");
    try {
      this.initializeFsmWorkers(this.subscriptionConfigs);
      this.readTradeDataAndProcess();
    } catch (CrudProjectException e) {
      log.error("Exception occurred ", e);
      throw e;
    }
    return true;
  }

  private void initializeFsmWorkers(List<SubscriptionConfig> subscriptionConfigs)
      throws IOException {
    log.debug("initializing FSM workers for subscriptionConfigs: {}", subscriptionConfigs);
    if (CollectionUtils.isNotEmpty(subscriptionConfigs)) {
      Queue<BarModel> webSocketWorkerQueue = new LinkedBlockingQueue<>();
      WebSocketWorker webSocketWorker = new WebSocketWorker(webSocketWorkerQueue);
      for (SubscriptionConfig subscriptionConfig : subscriptionConfigs) {
        Queue<TradeModel> tradeModelQueue = new LinkedBlockingQueue<>();
        subscriptionConfig.validateAndSetDefault();
        FsmWorker fsmWorker =
            new FsmWorker(tradeModelQueue, subscriptionConfig, webSocketWorkerQueue);
        fsmWorker.start();
        List<Queue<TradeModel>> tradeFsmQueue =
            tradeModelQueueHolder.getOrDefault(subscriptionConfig.getSymbol(), new ArrayList<>());
        tradeFsmQueue.add(tradeModelQueue);
        tradeModelQueueHolder.put(subscriptionConfig.getSymbol(), tradeFsmQueue);
        webSocketWorker.start();
      }
    }
  }

  private void readTradeDataAndProcess() throws IOException {
    log.debug("reading trading data and processing");
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(this.file));
      String line = null;
      while (null != (line = br.readLine())) {
        if (StringUtils.isNotEmpty(line)) {
          TradeModel tradeModel = CommonConstant.OBJECT_MAPPER.readValue(line, TradeModel.class);

          if (CollectionUtils.isNotEmpty(tradeModelQueueHolder.get(tradeModel.getSym()))) {
            tradeModelQueueHolder.get(tradeModel.getSym()).stream().forEach(e -> e.add(tradeModel));
          }
        }
      }
    } catch (Exception e) {
      log.error("Exception occurred while ", e);
    } finally {
      if (null != br) {
        br.close();
      }
    }
  }

}
