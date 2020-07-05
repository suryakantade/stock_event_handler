package com.surya.crudproject.common.fileutil.model;

import com.surya.crudproject.common.fileutil.constants.FileMappingConstants;

import java.util.Arrays;
import java.util.List;


public enum FileMappingType {
  STORED_DATA(FileMappingConstants.SUBSCRIPTION_CONFIG,
      FileMappingConstants.SUBSCRIPTION_CONFIG_FILE_PATH, Boolean.TRUE),
  TRADE_DATA(FileMappingConstants.TRADE_DATA, FileMappingConstants.TRADE_DATA_FILE_PATH,
      Boolean.FALSE);

  public String workflow;
  public String filePath;
  public Boolean loadOnInit;

  FileMappingType(String workflow, String filePath, Boolean loadOnInit) {
    this.workflow = workflow;
    this.filePath = filePath;
    this.loadOnInit = loadOnInit;
  }

  FileMappingType(String indicesName, String filePath) {
    this.workflow = indicesName;
    this.filePath = filePath;
  }

  public static List<FileMappingType> getFileMappingList() {
    return Arrays.asList(FileMappingType.values());
  }

  public static FileMappingType findFileMappingType(String docType) {
    return Arrays.stream(values()).filter(value -> value.workflow.equals(docType)).findFirst()
        .orElse(null);
  }
}
