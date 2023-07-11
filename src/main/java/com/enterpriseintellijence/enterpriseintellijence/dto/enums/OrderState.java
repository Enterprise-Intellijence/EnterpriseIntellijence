package com.enterpriseintellijence.enterpriseintellijence.dto.enums;

public enum OrderState {

    CANCELED,

    PENDING,
    PURCHASED, // once the payment is done
    SHIPPED, // for the whole shipment duration
    DELIVERED, // once the shipment is done
    COMPLETED, // once the product is received
    REVIEWED, // once the review is done

}
