import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createRequestOption } from '../../shared/util/request-util';

import APP_CONFIG from '../../app.config';
import { IResult } from '../model/result.model';

type EntityResponseType = HttpResponse<IResult>;
type EntityArrayResponseType = HttpResponse<IResult[]>;

@Injectable({ providedIn: 'root' })
export class ResultService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/result/';

    constructor(private http: HttpClient) {}

    create(contact: IResult): Observable<EntityResponseType> {
        return this.http.post<IResult>(this.resourceUrl, contact, { observe: 'response' });
    }

    update(contact: IResult): Observable<EntityResponseType> {
        return this.http.put<IResult>(this.resourceUrl, contact, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IResult>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IResult[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}