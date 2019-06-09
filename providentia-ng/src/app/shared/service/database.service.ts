import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createRequestOption } from '../../shared/util/request-util';

import APP_CONFIG from '../../app.config';
import { IDatabase } from '../model/database.model';

type EntityResponseType = HttpResponse<IDatabase>;
type EntityArrayResponseType = HttpResponse<IDatabase[]>;

@Injectable({ providedIn: 'root' })
export class DatabaseService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/database/';

    constructor(private http: HttpClient) {}

    create(database: IDatabase): Observable<EntityResponseType> {
        return this.http.post<IDatabase>(this.resourceUrl, database, { observe: 'response' });
    }

    update(database: IDatabase): Observable<EntityResponseType> {
        return this.http.put<IDatabase>(this.resourceUrl, database, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IDatabase>(`${this.resourceUrl}${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IDatabase[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}${id}`, { observe: 'response' });
    }
}
