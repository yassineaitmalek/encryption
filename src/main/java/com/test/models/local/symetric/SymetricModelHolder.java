package com.test.models.local.symetric;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SymetricModelHolder<T extends Serializable> {

  private T model;

  private String secretKey;

  private SymetricType symetricType;

}
