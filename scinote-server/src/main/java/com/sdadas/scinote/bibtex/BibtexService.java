package com.sdadas.scinote.bibtex;


import com.sdadas.scinote.shared.model.paper.Paper;

import java.util.Collection;

/**
 * @author Sławomir Dadas
 */
public interface BibtexService {

    String getBibTeX(Collection<Paper> papers);
}
