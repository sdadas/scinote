package com.sdadas.scinote.repos;

import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import com.sdadas.scinote.shared.model.paper.Paper;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
public interface ReposService {

    List<Paper> query(String query);

    ParseResponse parse(ParseRequest request);
}
