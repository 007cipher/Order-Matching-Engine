package com.cipher.matching.engine.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InstrumentService {

    private static final List<String> instruments = new ArrayList<>();

    static {
        instruments.add("BTCINR");
        instruments.add("ETHINR");
        instruments.add("LTCINR");
        instruments.add("XRPINR");
        instruments.add("ETHBTC");
        instruments.add("XRPBTC");
        instruments.add("LTCBTC");
        instruments.add("LTCETH");
        instruments.add("XRPETH");
    }

    public List<String> getInstruments() {
        return Collections.unmodifiableList(instruments);
    }

}