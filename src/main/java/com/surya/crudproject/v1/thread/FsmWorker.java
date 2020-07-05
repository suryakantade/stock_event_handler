package com.surya.crudproject.v1.thread;

import com.surya.crudproject.v1.model.BarModel;
import com.surya.crudproject.v1.model.SubscriptionConfig;
import com.surya.crudproject.v1.model.TradeModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;


@Slf4j
public class FsmWorker extends Thread {

  private SubscriptionConfig subscriptionConfig;
  private Queue<TradeModel> feedQueue;
  private BarModel barModel;
  private Long currentBarEndTime;
  private Queue<BarModel> webSocketWorkerQueue;

  public FsmWorker(Queue<TradeModel> feedQueue, SubscriptionConfig subscriptionConfig,
      Queue<BarModel> webSocketWorkerQueue) {
    this.feedQueue = feedQueue;
    this.barModel = new BarModel(subscriptionConfig.getEvent(), subscriptionConfig.getSymbol());
    this.subscriptionConfig = subscriptionConfig;
    this.barModel.setDefaultModel();
    this.webSocketWorkerQueue = webSocketWorkerQueue;
  }

  public void setCurrentBarEndTime(Long firstTradeStartTime) {
    log.debug("setting up current bar end time for firstTradeStartTime: {}", firstTradeStartTime);
    this.currentBarEndTime = null == this.currentBarEndTime ?
        firstTradeStartTime + subscriptionConfig.getInterval() :
        this.currentBarEndTime;
  }

  @Override
  public void run() {
    log.info("Running FSM thread");
    while (true) {
      if (!feedQueue.isEmpty()) {
        TradeModel tradeModel = feedQueue.poll();
        verifyAndProcessAgainstCurrentBar(tradeModel);
      }
    }
  }

  private void verifyAndProcessAgainstCurrentBar(TradeModel tradeModel) {
    log.debug("Verifying and processing tradeModel: {} against current bar", tradeModel);
    setCurrentBarEndTime(tradeModel.getTS2());
    if (tradeModel.getTS2() <= this.currentBarEndTime) {
      if (tradeModel.getSym().equals(subscriptionConfig.getSymbol())) {
        this.calculateOHLC(tradeModel);
        this.notifyThroughWebSocket();
      }
    } else {
      endBarAndCreateNewOne();
      verifyAndProcessAgainstCurrentBar(tradeModel);
    }
  }

  private void endBarAndCreateNewOne() {
    log.debug("finishing the current bar : {}", this.barModel);
    this.currentBarEndTime += this.subscriptionConfig.getInterval();
    this.barModel.setLastTradedPriceToClose();
    this.notifyThroughWebSocket();
    this.barModel.setDefaultModel();
  }

  private void calculateOHLC(TradeModel tradeModel) {
    if (barModel.getCurrentStockNo() == 0) {
      barModel.setO(tradeModel.getP());
      barModel.setL(tradeModel.getP());
    }
    if (null == barModel.getL() || barModel.getL() > tradeModel.getP()) {
      barModel.setL(tradeModel.getP());
    }
    if (null == barModel.getH() || barModel.getH() < tradeModel.getP()) {
      barModel.setH(tradeModel.getP());
    }
    barModel.setLastTradedPrice(tradeModel.getP());
    barModel.incrementCurrentStockNo();
    barModel.setVolume(null != barModel.getVolume() ?
        barModel.getVolume() + tradeModel.getQ() :
        tradeModel.getQ());
  }

  private void notifyThroughWebSocket() {
    log.debug("notifying through web socket : {}", this.barModel);
    this.webSocketWorkerQueue.add((BarModel) this.barModel.clone());
  }
}
