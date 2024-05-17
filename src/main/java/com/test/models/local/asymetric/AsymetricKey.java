package com.test.models.local.asymetric;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsymetricKey {

  private String publicKey;

  private String privateKey;

  private AsymetricType asymetricType;

}
