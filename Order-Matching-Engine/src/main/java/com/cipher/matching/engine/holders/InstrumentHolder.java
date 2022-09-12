package com.cipher.matching.engine.holders;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class InstrumentHolder {

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

    public static List<String> getInstruments() {
        return Collections.unmodifiableList(instruments);
    }

    public static boolean validateInstrument(String instrument) {
        log.debug("Instruments: {}", instruments);
        return instruments.contains(instrument);
    }
}