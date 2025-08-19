package com.github.navelogic.conversor_one.Service;

import com.github.navelogic.conversor_one.Util.Currency;
import com.github.navelogic.conversor_one.Util.ExchangeRateResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    @Value("${API.KEY}")
    private String apiKey;

    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    private final HttpClient httpClient;
    private final Gson gson;

    private final Map<String, ExchangeRateResponse> cache = new HashMap<>();
    private long lastCacheUpdate = 0;
    private static final long CACHE_DURATION = 5 * 60 * 1000;

    private final Map<String, Map<String, Double>> fallbackRates;

    public CurrencyService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
        this.fallbackRates = initializeFallbackRates();
    }

    private Map<String, Map<String, Double>> initializeFallbackRates() {
        Map<String, Map<String, Double>> rates = new HashMap<>();

        // Base para fallback
        Map<String, Double> usdRates = new HashMap<>();
        usdRates.put("ARS", 365.0);
        usdRates.put("BOB", 6.91);
        usdRates.put("BRL", 5.15);
        usdRates.put("CLP", 850.0);
        usdRates.put("COP", 4200.0);
        usdRates.put("USD", 1.0);
        rates.put("USD", usdRates);

        for (Currency currency : Currency.values()) {
            if (currency != Currency.USD) {
                Map<String, Double> currencyRates = new HashMap<>();
                double baseRate = usdRates.get(currency.name());

                for (Currency target : Currency.values()) {
                    if (target == currency) {
                        currencyRates.put(target.name(), 1.0);
                    } else {
                        double targetRate = usdRates.get(target.name());
                        currencyRates.put(target.name(), targetRate / baseRate);
                    }
                }
                rates.put(currency.name(), currencyRates);
            }
        }
        return rates;
    }

    public double convertCurrency(Currency from, Currency to, double amount) {
        if (from == to) {
            return amount;
        }

        try {
            ExchangeRateResponse response = getExchangeRates(from);
            if (response != null && response.isSuccess()) {
                Double rate = response.getConversionRates().get(to.name());
                if (rate != null) {
                    return amount * rate;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar taxas da API: " + e.getMessage());
        }

        return convertWithFallback(from, to, amount);
    }

    private double convertWithFallback(Currency from, Currency to, double amount) {
        Map<String, Double> fromRates = fallbackRates.get(from.name());
        if (fromRates != null && fromRates.containsKey(to.name())) {
            return amount * fromRates.get(to.name());
        }
        return amount;
    }

    private ExchangeRateResponse getExchangeRates(Currency baseCurrency) {
        String cacheKey = baseCurrency.name();
        long currentTime = System.currentTimeMillis();

        if (cache.containsKey(cacheKey) &&
                (currentTime - lastCacheUpdate) < CACHE_DURATION) {
            return cache.get(cacheKey);
        }

        String url = BASE_URL + apiKey + "/latest/" + baseCurrency.name();

        ExchangeRateResponse response = fetchFromApi(url);

        if (response != null && response.isSuccess()) {
            cache.put(cacheKey, response);
            lastCacheUpdate = currentTime;
        }

        return response;
    }

    private ExchangeRateResponse fetchFromApi(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), ExchangeRateResponse.class);
            }
        } catch (Exception e) {
            System.err.println("Erro ao acessar API principal: " + e.getMessage());
        }
        return null;
    }

    public String getLastUpdateTime() {
        try {
            ExchangeRateResponse response = getExchangeRates(Currency.USD);
            if (response != null && response.getTimeLastUpdateUtc() != null) {
                return response.getTimeLastUpdateUtc();
            }
        } catch (Exception e) {
            System.err.println("Erro ao obter tempo de atualização: " + e.getMessage());
        }
        return "Informação não disponível";
    }

    public boolean isApiAvailable() {
        try {
            ExchangeRateResponse response = getExchangeRates(Currency.USD);
            return response != null && response.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }
}
