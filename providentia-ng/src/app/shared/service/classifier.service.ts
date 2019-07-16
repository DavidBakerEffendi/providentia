import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../app.config';

@Injectable({ providedIn: 'root' })
export class ClassifierService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/classifier/';

    public SENTIMENT_MODE = 'sentiment';
    public FAKE_MODE = 'fake';

    constructor(private http: HttpClient) { }

    testReady(mode: string): Observable<HttpResponse<any>> {
        return this.http.get<any>(`${this.resourceUrl}${mode}`, { observe: 'response' });
    }

    submitText(text: string, mode: string): Observable<HttpResponse<any>> {
        return this.http.post<any>(`${this.resourceUrl}${mode}`, { 'text': text }, { observe: 'response' });
    }
}