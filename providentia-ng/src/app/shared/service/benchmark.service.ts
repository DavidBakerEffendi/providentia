import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createRequestOption } from '../util/request-util';

import APP_CONFIG from '../../app.config';
import { IBenchmark } from '../model/benchmark.model';

type EntityResponseType = HttpResponse<IBenchmark>;
type EntityArrayResponseType = HttpResponse<IBenchmark[]>;

@Injectable({ providedIn: 'root' })
export class BenchmarkService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/benchmark/';

    constructor(private http: HttpClient) {}

    create(benchmark: IBenchmark): Observable<EntityResponseType> {
        return this.http.post<IBenchmark>(this.resourceUrl, benchmark, { observe: 'response' });
    }

    update(benchmark: IBenchmark): Observable<EntityResponseType> {
        return this.http.put<IBenchmark>(this.resourceUrl, benchmark, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IBenchmark>(`${this.resourceUrl}${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IBenchmark[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}${id}`, { observe: 'response' });
    }
}