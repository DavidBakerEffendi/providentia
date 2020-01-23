import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../../app.config';
import { ISim3Result } from '../../model/analysis/sim3.model';

type EntityArrayResponseType = HttpResponse<ISim3Result[]>;

@Injectable({ providedIn: 'root' })
export class Sim3Service {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/result/sim3/';
    public analysisId = '2d8ca3c7-ab16-4567-a821-1d480ce19bfa';

    constructor(private http: HttpClient) {}

    getResults(benchmarkId: string): Observable<EntityArrayResponseType> {
        return this.http.get<ISim3Result[]>(`${this.resourceUrl}${benchmarkId}`, { observe: 'response' });
    }

}
