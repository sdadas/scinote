package com.sdadas.scinote.shared.model.paper;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

/**
 * @author Sławomir Dadas
 */
public class PaperIdDeserializer extends KeyDeserializer {

    @Override
    public PaperId deserializeKey(String key, DeserializationContext context) {
        return PaperId.fromString(key, ",");
    }
}
