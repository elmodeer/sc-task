package com.scalable.capital.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.scalable.capital.model.Currency;
import com.scalable.capital.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.UnsupportedCharsetException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.google.gson.JsonParser;

@Service
@Slf4j
public class ExchangeService {

    private static String EXR_TYPE = ".EUR.SP00.A";
    private static String API_BASE_URL = "https://sdw-wsrest.ecb.europa.eu/service/data/EXR/D.";
    private static String INTERACTIVE_CHART_URL = "https://sdw.ecb.europa.eu/quickview.do?SERIES_KEY=120.EXR.D.";
    private static String API_BASE_PARAMS = EXR_TYPE + "?startPeriod=" + mostRecentDay() + "&detail=dataOnly&format=jsondata";
    private static Map<String, AtomicInteger> supportedCurrencies = initSupportedList();

    public BigDecimal getEXRWithEuroBase(String currency) throws IOException, InterruptedException {
        if (isSupported(currency)) {
            var response = doRequest(API_BASE_URL + currency + API_BASE_PARAMS);
            return JsonUtil.get(response, "dataSets.series.0:0:0:0:0.observations.0");
        }
        return null;
    }


//    Kundennummer 17831105

    public BigDecimal getEXR(String currencyPair) throws IOException, InterruptedException {
        var pair = currencyPair.split("/");
        var eurFirst = getEXRWithEuroBase(pair[0]);
        var eurSecond = getEXRWithEuroBase(pair[1]);
        return eurSecond.divide(eurFirst, MathContext.DECIMAL32);
    }

    public String formatInteractiveChartLink(String targetCurrency) {
        if (isSupported(targetCurrency)) {
            return INTERACTIVE_CHART_URL + targetCurrency + EXR_TYPE;
        } return null;
    }

    public Map<String, AtomicInteger> getSupportedCurrenciesWithRequestFreq() {
        return supportedCurrencies;
    }

    private JsonElement doRequest(String url) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return JsonParser.parseString(response.body().toString()).getAsJsonObject();
    }

    private static Map<String, AtomicInteger> initSupportedList() {
        return Arrays.stream(Currency.values())
                .collect(Collectors.toMap(Currency::name, e -> new AtomicInteger()));
    }

    private static String mostRecentDay() {
        // check later for TARGET closing days.
        var lastDay = LocalDateTime.now();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (lastDay.getHour() > 16) {
            return lastDay.format(formatter);
        } else {
            return lastDay.minusDays(1).format(formatter);
        }
    }

    private boolean isSupported(String currency) {
        // log request count
        var isSupported = Arrays.stream(Currency.values()).filter(e -> e.name().equals(currency)).count() != 0;
        if (isSupported) {
            supportedCurrencies.get(currency).incrementAndGet();
            return true;
        } return false;
    }
}
