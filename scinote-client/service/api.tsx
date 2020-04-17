import {ActionResponse, Paper, PaperActionRequest, Project, ProjectActionRequest, ProjectInfo} from "../model";

class APIService {

    public baseURL: string;

    constructor() {
        this.baseURL = APIService.createBaseURL()
    }

    private static createBaseURL(): string {
        const host = window.location.hostname;
        if (host.indexOf("localhost") >= 0) {
            if(window.location.port == "1234") return "http://localhost:8080/";
            return window.location.protocol + "//localhost:" + window.location.port + "/";
        } else {
            return window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + "/";
        }
    }

    public search(query: string): Promise<Paper[]> {
        return fetch(`${this.baseURL}/search?q=${query}`).then((res) => res.json());
    }

    public papers(ids: string[]): Promise<Paper[]> {
        const query = ids.map(val => `id=${val}`).join("&");
        return fetch(`${this.baseURL}/papers?${query}`).then((res) => res.json());
    }

    public parseUrl(): string {
        return `${this.baseURL}/parse`;
    }

    public projectList(): Promise<ProjectInfo[]> {
        return fetch(`${this.baseURL}/project/list`).then((res) => res.json());
    }

    public projectDetails(projectId: string): Promise<Project> {
        return fetch(`${this.baseURL}/project/${projectId}`).then((res) => res.json());
    }

    public projectAction(request: ProjectActionRequest): Promise<ActionResponse> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const init: RequestInit = {method: "POST", body: JSON.stringify(request), headers: headers};
        return fetch(`${this.baseURL}/project/action`, init).then((res) => res.json());
    }

    public paperAction(request: PaperActionRequest): Promise<ActionResponse> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const init: RequestInit = {method: "POST", body: JSON.stringify(request), headers: headers};
        return fetch(`${this.baseURL}/project/paper/action`, init).then((res) => res.json());
    }
}

export const api: APIService = new APIService();
