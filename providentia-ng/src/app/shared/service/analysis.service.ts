import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createRequestOption } from '../../shared/util/request-util';

import APP_CONFIG from '../../app.config';
import { IAnalysis, IAnalysisPerf } from '../model/analysis.model';

type EntityResponseType = HttpResponse<IAnalysis>;
type EntityArrayResponseType = HttpResponse<IAnalysis[]>;

@Injectable({ providedIn: 'root' })
export class AnalysisService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/analysis/';

    constructor(private http: HttpClient) {}

    create(analysis: IAnalysis): Observable<EntityResponseType> {
        return this.http.post<IAnalysis>(this.resourceUrl, analysis, { observe: 'response' });
    }

    update(analysis: IAnalysis): Observable<EntityResponseType> {
        return this.http.put<IAnalysis>(this.resourceUrl, analysis, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IAnalysis>(`${this.resourceUrl}${id}`, { observe: 'response' });
    }

    performance(id: string): Observable<HttpResponse<IAnalysisPerf[]>> {
        return this.http.get<IAnalysisPerf[]>(`${this.resourceUrl}performance/${id}`, { observe: 'response' })
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IAnalysis[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}${id}`, { observe: 'response' });
    }
}
