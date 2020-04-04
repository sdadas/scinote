package com.sdadas.scinote.repos.shared;

import com.sdadas.scinote.repos.shared.model.Paper;
import com.sdadas.scinote.repos.shared.model.PaperId;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
public interface RepoClient {

    String repoId();

    int priority();

    PaperId supports(String query);

    List<Paper> search(String query) throws Exception;

    List<Paper> load(List<PaperId> ids) throws Exception;
}
