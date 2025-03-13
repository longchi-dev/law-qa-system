package com.law.law_qa_system.dto;

import lombok.Builder;

public abstract class PaymentDTO {
    @Builder
    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;

        // Constructor tường minh với quyền truy cập public
        public VNPayResponse(String code, String message, String paymentUrl) {
            this.code = code;
            this.message = message;
            this.paymentUrl = paymentUrl;
        }
    }
}
