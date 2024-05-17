package com.test.models.local.symetric;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SymetricDataOutput {

  private String output;

  private SymetricType symetricType;
}
