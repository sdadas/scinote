import {Paper} from "../model";

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
}

export const api: APIService = new APIService();
