import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../../app.config';
import { ISim2Result } from '../../model/analysis/sim2.model';

type EntityArrayResponseType = HttpResponse<ISim2Result[]>;

@Injectable({ providedIn: 'root' })
export class Sim2Service {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/result/sim2/';
    public analysisId = '34a6d0e2-ca77-4615-a873-9a0d0b92559b';

    constructor(private http: HttpClient) {}

    getResults(benchmarkId: string): Observable<EntityArrayResponseType> {
        return this.http.get<ISim2Result[]>(`${this.resourceUrl}${benchmarkId}`, { observe: 'response' });
    }

}
