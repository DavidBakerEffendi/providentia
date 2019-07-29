import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../app.config';
import { IQuery } from '../model/query.model';

type EntityArrayResponseType = HttpResponse<IQuery[]>;

@Injectable({ providedIn: 'root' })
export class QueryService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/queries/';

    constructor(private http: HttpClient) {}

    getQueries(analysisId: string, databaseId: string): Observable<EntityArrayResponseType> {
        return this.http.get<IQuery[]>(`${this.resourceUrl}${analysisId}+${databaseId}`, { observe: 'response' });
    }

}
