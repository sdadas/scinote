package com.sdadas.scinote.project.model;

import com.sdadas.scinote.shared.model.paper.PaperId;

/**
 * @author Sławomir Dadas
 */
public interface PaperBaseAction extends ProjectBaseAction {

    PaperId getPaperId();
}
