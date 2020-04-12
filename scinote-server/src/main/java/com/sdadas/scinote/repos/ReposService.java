package com.sdadas.scinote.repos;

import com.sdadas.scinote.repos.parse.model.ParseRequest;
import com.sdadas.scinote.repos.parse.model.ParseResponse;
import com.sdadas.scinote.shared.model.paper.Paper;
import com.sdadas.scinote.shared.model.paper.PaperId;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
public interface ReposService {

    List<Paper> papersByQuery(String query);

    List<Paper> papersByIds(List<PaperId> ids);

    ParseResponse parse(ParseRequest request);
}
