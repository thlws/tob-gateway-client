package io.github.thlws.gateway.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author tanghl@msn.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TobTokenResult implements Serializable {

    private TobToken data;

    private Integer resultCode;

    private String errMsg;

}
