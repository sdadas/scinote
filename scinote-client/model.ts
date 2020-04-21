
export interface PaperId {
    repo: string;
    id: string;
}

export interface Paper {
    ids: PaperId[];
    title: string;
    titleNorm?: string;
    type: string;
    summary?: string;
    authors: Author[];
    languages?: string[];
    year?: number;
    date?: string;
    source: Source;
    urls: WebLocation[];
    citations?: number;
    pages?: string;
    doi?: string;
    keywords?: string[];
    cachedText: Set<string>;
}

export interface Author {
    id?: string;
    firstName: string;
    lastName: string;
    affiliation?: string;
    order: number;
}

export interface Source {
    name?: string;
    publisher?: string;
    venue?: string;
    venueShort?: string;
}

export interface WebLocation {
    name: string;
    url: string;
}

export interface ProjectInfo {
    id: string;
    title: string;
    updated: string;
}

export interface Project {
    id: string;
    title: string;
    accepted: ProjectPaper[];
    rejected: ProjectPaper[];
    readLater: ProjectPaper[];
}

export interface ProjectPaper {
    id: PaperId;
    notes?: string;
    tags: string[];
    added: number;
}

export interface PaperDetails {
    key: string;
    paper: Paper;
    projectPaper: ProjectPaper;
}

export interface ActionResponse {
    errors: string[];
    result?: any;
}

export interface ProjectActionRequest {
    projectId: string;
    action: "CREATE" | "DELETE";
}

export interface PaperActionRequest {
    projectId: string;
    paperId: PaperId;
    action: "ACCEPT" | "REJECT" | "READ_LATER";
}

export interface EditProjectRequest {
    projectId: string;
    title: string;
}

export interface EditPaperRequest {
    projectId?: string;
    paperId: PaperId;
    notes?: string;
    tags?: string[];
}

export interface UIAction {
    type: "SEARCH" | "PROJECT_CHANGED"
    timestamp?: number;
    payload?: any;
}
