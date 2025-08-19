package com.github.navelogic.conversor_one.Util;

import lombok.Getter;

@Getter
public enum Currency {
    ARS("Peso Argentino", "$"),
    BOB("Boliviano Boliviano", "Bs"),
    BRL("Real Brasileiro", "R$"),
    CLP("Peso Chileno", "$"),
    COP("Peso Colombiano", "$"),
    USD("Dólar Americano", "$");

    private final String name;
    private final String symbol;

    Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }
}
