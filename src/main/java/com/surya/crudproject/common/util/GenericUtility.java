package com.surya.crudproject.common.util;

import com.surya.crudproject.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Slf4j
@Component("com.surya.crudproject.common.util.GenericUtility")
public class GenericUtility {

  public static Long findTimeStampFromStringDate(String date){
    SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstant.DATE_FORMAT);
    Long longTime = null;
    try {
      Date d = dateFormat.parse(date);
      longTime = d.getTime();
    } catch (ParseException e) {
      log.error("Error occurred while parsing date : {}", date);
    }
    return longTime;
  }

}
