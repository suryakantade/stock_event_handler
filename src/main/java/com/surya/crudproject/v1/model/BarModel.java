package com.surya.crudproject.v1.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.surya.crudproject.common.exception.CrudProjectException;
import com.surya.crudproject.common.model.CrudProjectStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(doNotUseGetters = true, exclude = {"lastTradedPrice", "currentStockNo"})
@JsonIgnoreProperties(ignoreUnknown = true, value = {"lastTradedPrice"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BarModel implements Cloneable{
  private Double o;
  private Double h;
  private Double l;
  private Double c;
  @JsonIgnore
  private Double lastTradedPrice;
  private Double volume;
  private String event;
  private String symbol;
  private Integer bar_num;
  @JsonIgnore
  private Integer currentStockNo;

  public void setDefaultModel() {
    if (null == this.bar_num) {
      this.bar_num = 0;
    }
    this.bar_num += 1;
    this.o = null;
    this.h = null;
    this.l = null;
    this.c = null;
    this.volume = null;
    this.lastTradedPrice = null;

    this.currentStockNo = 0;
  }

  public void incrementCurrentStockNo() {
    this.setCurrentStockNo(this.getCurrentStockNo() + 1);
  }

  public void setLastTradedPriceToClose() {
    this.setC(this.getLastTradedPrice());
  }

  public BarModel(String event, String symbol) {
    this.event = event;
    this.symbol = symbol;
  }

  public Object clone(){
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new CrudProjectException(CrudProjectStatusCode.PROCESSING_ERROR);
    }
  }
}
