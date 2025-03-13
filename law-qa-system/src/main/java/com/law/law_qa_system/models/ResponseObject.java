package com.law.law_qa_system.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject {
    private String status;
    private String message;
    private Object data;
}
