import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../app.config';
import { IServerLog } from '../../shared';

type EntityArrayResponseType = HttpResponse<IServerLog[]>;

@Injectable({ providedIn: 'root' })
export class LogService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/logs/';

    constructor(private http: HttpClient) {}

    getRecent(): Observable<EntityArrayResponseType> {
        return this.http.get<IServerLog[]>(this.resourceUrl, { observe: 'response' });
    }
}