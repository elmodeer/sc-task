package com.scalable.capital.controller;

import com.scalable.capital.model.Currency;
import com.scalable.capital.service.ExchangeService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping(value = "/exchange")
@AllArgsConstructor
@Slf4j
public class ExchangeController {

    private ExchangeService exchangeService;

    @GetMapping("/ref")
    public ResponseEntity<String> getEXRWithEuroBase(@NotNull @RequestParam("currencyPair") String currencyPair) throws IOException, InterruptedException {
        var pair = currencyPair.split("/");
        var euroBase = pair[0].equals(Currency.EUR.name());
        BigDecimal val;
        if (euroBase)
            val = exchangeService.getEXRWithEuroBase(pair[1]);
        else
            val = exchangeService.getEXR(currencyPair);

        return val != null ?
                new ResponseEntity<>(val.toString(), HttpStatus.OK) :
                new ResponseEntity<>("unsupported currency pair: " + currencyPair, HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/convert")
    public ResponseEntity<String> convert(@RequestParam("euroAmount") Integer euroAmount,
                                          @RequestParam("targetCurrency") String targetCurrency) throws IOException, InterruptedException {
        // will assume all conversion are euro based for simplicity
        var exr = exchangeService.getEXRWithEuroBase(targetCurrency);
        var  result = exr.multiply(BigDecimal.valueOf(euroAmount));
        return result != null ?
                new ResponseEntity<>(result.toString(), HttpStatus.OK) :
                new ResponseEntity<>("unsupported currency pair: " + targetCurrency, HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/interactiveChart")
    public ResponseEntity<String> getInteractiveChart(@NotNull @RequestParam("targetCurrency") String targetCurrency) {
        // will assume all charts are euro based for simplicity, and actually is not clear for me with this api
        // how i could change that and get a chart for something like USD/HUF.
        var result = exchangeService.formatInteractiveChartLink(targetCurrency);
        return result != null ?
                new ResponseEntity<>(result, HttpStatus.OK) :
                new ResponseEntity<>("unsupported currency pair: " + targetCurrency, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/supported")
    public ResponseEntity<Map<String, AtomicInteger>> getSupportedCurrenciesWithRequestFreq() {
        var supportedCurrencies = exchangeService.getSupportedCurrenciesWithRequestFreq();

        // better response formatting can be done later
        return supportedCurrencies != null ?
                new ResponseEntity<>(supportedCurrencies, HttpStatus.OK) :
                new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
    }


}
