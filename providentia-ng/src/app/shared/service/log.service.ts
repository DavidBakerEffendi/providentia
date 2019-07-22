import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import APP_CONFIG from '../../app.config';
import { IServerLog } from '../../shared';

type EntityArrayResponseType = HttpResponse<IServerLog[]>;

@Injectable({ providedIn: 'root' })
export class LogService {
    private resourceUrl = APP_CONFIG.FLASK_API + 'api/logs/';

    constructor(private http: HttpClient) {
        Date.prototype.toISOString = function() {
            var tzo = -this.getTimezoneOffset(),
                dif = tzo >= 0 ? '+' : '-',
                pad = function(num) {
                    var norm = Math.floor(Math.abs(num));
                    return (norm < 10 ? '0' : '') + norm;
                };
            return this.getFullYear() +
                '-' + pad(this.getMonth() + 1) +
                '-' + pad(this.getDate()) +
                'T' + pad(this.getHours()) +
                ':' + pad(this.getMinutes()) +
                ':' + pad(this.getSeconds()) +
                dif + pad(tzo / 60) +
                ':' + pad(tzo % 60);
        }
    }

    getRecent(): Observable<EntityArrayResponseType> {
        return this.http.get<IServerLog[]>(this.resourceUrl, { observe: 'response' });
    }

    getFromTo(dateFrom: Date, dateTo: Date): Observable<EntityArrayResponseType> {
        return this.http.post<IServerLog[]>(this.resourceUrl, { 'from': dateFrom.toISOString(), 'to': dateTo.toISOString() }, { observe: 'response' });
    }

}