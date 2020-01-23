import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../../app.config';
import { ISim1Result } from '../../model/analysis/sim1.model';

type EntityArrayResponseType = HttpResponse<ISim1Result[]>;

@Injectable({ providedIn: 'root' })
export class Sim1Service {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/result/sim1/';
    public analysisId = '899760bd-417e-431c-bac1-d5e4a8e16462';

    constructor(private http: HttpClient) {}

    getResults(benchmarkId: string): Observable<EntityArrayResponseType> {
        return this.http.get<ISim1Result[]>(`${this.resourceUrl}${benchmarkId}`, { observe: 'response' });
    }

}
