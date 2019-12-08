import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../../app.config';
import { ICitySentiment } from '../../model/analysis/city.model';

type EntityResponseType = HttpResponse<ICitySentiment>;

@Injectable({ providedIn: 'root' })
export class CitySentimentService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/result/city-sentiment/';
    public analysisId = '05c2c642-32c0-4e6a-a0e5-c53028035fc8';

    constructor(private http: HttpClient) {}

    getResult(benchmarkId: string): Observable<EntityResponseType> {
        return this.http.get<ICitySentiment>(`${this.resourceUrl}${benchmarkId}`, { observe: 'response' });
    }

}
