package com.surya.crudproject.v1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionConfig {
  private String event;
  private String symbol;
  private Long interval;

  public Boolean validateAndSetDefault() {
    Boolean isValid = Boolean.FALSE;
    if (!isAnyNull(this.getEvent(), this.getSymbol(), this.getInterval())) {
      interval = 1000000000 * interval;
      isValid = Boolean.TRUE;
    }
    return isValid;
  }

  private Boolean isAnyNull(Object... obj) {
    return Arrays.stream(obj).filter(o -> {
      return o == null;
    }).findFirst().isPresent();
  }
}
