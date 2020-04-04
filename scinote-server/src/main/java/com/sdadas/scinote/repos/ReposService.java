package com.sdadas.scinote.repos;

import com.sdadas.scinote.repos.shared.model.Paper;

import java.util.List;

/**
 * @author Sławomir Dadas
 */
public interface ReposService {

    List<Paper> query(String query);
}
