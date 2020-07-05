package com.surya.crudproject.v1.util;


import com.surya.crudproject.common.exception.CrudProjectException;
import com.surya.crudproject.common.fileutil.model.FileMappingType;
import com.surya.crudproject.common.fileutil.util.MappingFileUtil;
import com.surya.crudproject.v1.model.SubscriptionConfig;
import com.surya.crudproject.v1.thread.FileReaderWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Slf4j
@Component("com.surya.crudproject.v1.util.ReadAndNotifyUtil")
public class ReadAndNotifyUtil implements CommandLineRunner {

  @Autowired
  @Qualifier("com.surya.crudproject.common.fileutil.util.MappingFileUtil")
  private MappingFileUtil mappingFileUtil;

  @Override
  public void run(String... args) throws Exception {
    log.info("Started ReadAndNotifyUtil ");
    try {
      this.createReaderAndNotifierThread();
    } catch (Exception e) {
      log.error("Exception occurred while starting main thread : ", e);
    }

  }


  private void createReaderAndNotifierThread() {
    List<SubscriptionConfig> subscriptionConfigs =
        mappingFileUtil.getConfigList(FileMappingType.STORED_DATA);
    File file = mappingFileUtil.getFileObject(FileMappingType.TRADE_DATA);
    if (CollectionUtils.isNotEmpty(subscriptionConfigs) && file.exists()) {
      ExecutorService executorService = Executors.newFixedThreadPool(subscriptionConfigs.size());
      List<Future<Boolean>> futures = new ArrayList<>();
      try {
        futures.add(executorService.submit(new FileReaderWorker(file, subscriptionConfigs)));
      } catch (CrudProjectException e) {
        log.error("Exception occurred while creating thread ", e);
        throw e;
      }
      executorService.shutdown();
    }
  }
}
