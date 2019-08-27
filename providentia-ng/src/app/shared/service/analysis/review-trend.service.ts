import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../../app.config';
import { IReviewTrend } from '../../model/analysis/review-trend.model';

type EntityArrayResponseType = HttpResponse<IReviewTrend[]>;

@Injectable({ providedIn: 'root' })
export class ReviewTrendService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/result/review-trends/';
    public analysisId = 'b540a4dd-f010-423b-9644-aef4e9b754a9';

    constructor(private http: HttpClient) {}

    getResults(benchmarkId: string): Observable<EntityArrayResponseType> {
        return this.http.get<IReviewTrend[]>(`${this.resourceUrl}${benchmarkId}`, { observe: 'response' });
    }

}
