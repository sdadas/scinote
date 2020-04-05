package com.sdadas.scinote.repos.parse;

import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;

/**
 * @author Sławomir Dadas
 */
public interface PaperParserService {

    ParseResponse parse(ParseRequest request);
}
