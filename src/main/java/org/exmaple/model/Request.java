package org.exmaple.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Request {
    private Car car;
    private Date datePurchased;

    // standard getters setters
}