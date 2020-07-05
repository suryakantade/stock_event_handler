package com.surya.crudproject.common.fileutil.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surya.crudproject.common.exception.CrudProjectException;
import com.surya.crudproject.common.fileutil.model.FileMappingType;
import com.surya.crudproject.common.model.CrudProjectStatusCode;
import com.surya.crudproject.v1.model.SubscriptionConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("com.surya.crudproject.common.fileutil.util.MappingFileUtil")
public class MappingFileUtil {

  @Resource(name = "fileUtils")
  private FileUtils fileUtils;

  @Resource(name = "objectMapper")
  private ObjectMapper objectMapper;

  private Map<FileMappingType, String> storedFileData = null;

  private List<SubscriptionConfig> configList = null;


  /*
   * read all files while loading and keep it in a map
   *
   * */
  @PostConstruct
  public void init() {
    storedFileData = new HashMap<>();
    List<FileMappingType> fileMappings = FileMappingType.getFileMappingList();
    synchronized (this.storedFileData) {
      fileMappings.forEach(fileMapping -> {
        if (StringUtils.isNotBlank(fileMapping.filePath) && BooleanUtils
            .isTrue(fileMapping.loadOnInit)) {
          populate(fileMapping, fileMapping.filePath);
        }
      });
    }
  }

  private void populate(FileMappingType type, final String filePath) {
    try {

      String fileContent = fileUtils.readFileToString(new File(filePath), StandardCharsets.UTF_8);
      storedFileData.put(type, fileContent);
    } catch (IOException e) {
      log.error("exception occured while initializing file content {} exception:", type, e);
      throw new CrudProjectException(CrudProjectStatusCode.FILE_CONTENT_MAPPING_FAILED);
    }
  }

  public String findStoredDataContent(FileMappingType fileMapping) {
    if (null == this.storedFileData || StringUtils.isEmpty(this.storedFileData.get(fileMapping))) {
      this.init();
    }
    return this.storedFileData.get(fileMapping);
  }


  public List<SubscriptionConfig> getConfigList(FileMappingType fileMapping) {
    log.info("fetching updatable attributes for fileMapping: {}", fileMapping);

    if (CollectionUtils.isEmpty(configList)) {
      String fileContent = findStoredDataContent(fileMapping);
      try {
        configList =
            objectMapper.readValue(fileContent, new TypeReference<List<SubscriptionConfig>>() {
            });
      } catch (IOException e) {
        log.error("mapping from json to list failed");
      }
    }
    return configList;
  }

  public File getFileObject(FileMappingType fileMappingType) {
    return new File(fileMappingType.filePath);
  }
}

