package com.hand.hcf.app.common.co;

import lombok.Data;

import java.io.Serializable;

@Data
public class ClientCO implements Serializable {
    private String clientId;
    private String clientSecret;

}
