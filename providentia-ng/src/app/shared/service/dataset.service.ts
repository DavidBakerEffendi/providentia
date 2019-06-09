import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { createRequestOption } from '../../shared/util/request-util';

import APP_CONFIG from '../../app.config';
import { IDataset } from '../model/dataset.model';

type EntityResponseType = HttpResponse<IDataset>;
type EntityArrayResponseType = HttpResponse<IDataset[]>;

@Injectable({ providedIn: 'root' })
export class DatasetService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/dataset/';

    constructor(private http: HttpClient) {}

    create(dataset: IDataset): Observable<EntityResponseType> {
        return this.http.post<IDataset>(this.resourceUrl, dataset, { observe: 'response' });
    }

    update(dataset: IDataset): Observable<EntityResponseType> {
        return this.http.put<IDataset>(this.resourceUrl, dataset, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IDataset>(`${this.resourceUrl}${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IDataset[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}${id}`, { observe: 'response' });
    }
}
