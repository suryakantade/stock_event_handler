package com.surya.crudproject.common.fileutil.constants;

public class FileMappingConstants {
  public static final String MAPPING_FILE_DIR = "config/mapping/";

  public static final String STORED_DATA_MAPPING_DIR = "stored_data/";

  public static final String SUBSCRIPTION_CONFIG = "SUBSCRIPTION_CONFIG";
  public static final String SUBSCRIPTION_CONFIG_FILE_PATH =
      MAPPING_FILE_DIR + STORED_DATA_MAPPING_DIR + "subscription_config.txt";

  public static final String TRADE_DATA = "TRADE_DATA";
  public static final String TRADE_DATA_FILE_PATH =
      MAPPING_FILE_DIR + STORED_DATA_MAPPING_DIR + "trade_data.json";

}
