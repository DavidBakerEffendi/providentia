import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createRequestOption } from '../../shared/util/request-util';

import APP_CONFIG from '../../app.config';
import { IDatabase } from '../model/database.model';

type EntityArrayResponseType = HttpResponse<IDatabase[]>;

@Injectable({ providedIn: 'root' })
export class DatabaseService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/database/';

    constructor(private http: HttpClient) {}

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IDatabase[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    submitQuery(name: String, query: string):  Observable<HttpResponse<any>> {
        return this.http.post<any>(`${this.resourceUrl}query/${name}`, {'query': query}, { observe: 'response' });
    }
}
