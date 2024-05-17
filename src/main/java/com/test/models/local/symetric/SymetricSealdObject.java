package com.test.models.local.symetric;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SymetricSealdObject {

  private String sealedObject;

  private String secretKey;

  private SymetricType symetricType;

}
