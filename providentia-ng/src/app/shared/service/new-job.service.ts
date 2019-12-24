import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../app.config';
import { IBenchmark } from '../model/benchmark.model';

type EntityResponseType = HttpResponse<IBenchmark>;

@Injectable({ providedIn: 'root' })
export class NewJobService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/new-job/';

    constructor(private http: HttpClient) {}

    create(benchmark: IBenchmark, numJobs: number): Observable<EntityResponseType> {
        return this.http.post<IBenchmark>(`${this.resourceUrl}num-jobs/${numJobs}`, benchmark, { observe: 'response' });
    }

}
