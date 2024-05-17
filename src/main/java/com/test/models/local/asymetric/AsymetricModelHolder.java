package com.test.models.local.asymetric;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsymetricModelHolder<T extends Serializable> {

  private T model;

  private String publicKey;

  private AsymetricType asymetricType;

}
