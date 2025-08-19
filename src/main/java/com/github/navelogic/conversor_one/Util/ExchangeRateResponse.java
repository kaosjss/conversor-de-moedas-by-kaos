package com.github.navelogic.conversor_one.Util;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRateResponse {
    @SerializedName("result")
    private String result;

    @SerializedName("time_last_update_utc")
    private String timeLastUpdateUtc;

    @SerializedName("base_code")
    private String baseCode;

    @SerializedName("conversion_rates")
    private Map<String, Double> conversionRates;

    public boolean isSuccess() {
        return "success".equals(result);
    }
}
