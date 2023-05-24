package com.kucoin.sdk.rest.response;

import lombok.Data;

/**
 * @author Jason Yao
 * @version 1.0.0
 * @ClassName HFOrderSyncCancelResponse.java
 * @Description
 * @createTime 2023/05/23日 16:05:00
 */
@Data
public class HFOrderSyncCancelResponse {

    private String orderId;
    private String originSize;
    private String originFunds;
    private String dealSize;
    private String remainSize;
    private String canceledSize;
    private String status;
}
