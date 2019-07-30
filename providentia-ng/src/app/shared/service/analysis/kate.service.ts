import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../../app.config';
import { IKate } from '../../model/analysis/kate.model';

type EntityArrayResponseType = HttpResponse<IKate[]>;

@Injectable({ providedIn: 'root' })
export class KateService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/result/kate/';
    public analysisId = '81c1ab05-bb06-47ab-8a37-b9aeee625d0f';

    constructor(private http: HttpClient) {}

    getResults(benchmarkId: string): Observable<EntityArrayResponseType> {
        return this.http.get<IKate[]>(`${this.resourceUrl}${benchmarkId}`, { observe: 'response' });
    }

}
