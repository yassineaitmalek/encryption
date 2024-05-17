package com.test.models.local.asymetric;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsymetricSealdObject {

  private String sealedObject;

  private String privateKey;

  private AsymetricType asymetricType;

}
