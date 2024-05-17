package com.test.models.local;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestModel implements Serializable {

  private LocalDate date;

  private String str;

  private TestSubModel subModel;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TestSubModel implements Serializable {

    private String subStr;

  }
}
