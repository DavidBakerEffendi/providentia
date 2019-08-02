import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../app.config';

@Injectable({ providedIn: 'root' })
export class ClassifierService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/classifier/';

    constructor(private http: HttpClient) { }

    testReady(): Observable<HttpResponse<any>> {
        return this.http.get<any>(`${this.resourceUrl}`, { observe: 'response' });
    }

    submitText(text: string): Observable<HttpResponse<any>> {
        return this.http.post<any>(`${this.resourceUrl}`, { 'text': text }, { observe: 'response' });
    }
}