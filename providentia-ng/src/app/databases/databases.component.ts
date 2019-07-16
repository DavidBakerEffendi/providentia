import { Component, OnInit } from '@angular/core';

import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IDatabase, DatabaseService, InfoMessage } from '../shared';

@Component({
    selector: 'prv-databases',
    templateUrl: './databases.component.html',
    styleUrls: ['databases.scss']
})
export class DatabasesComponent extends InfoMessage implements OnInit {

    databases: IDatabase[];
    queryResponses = new Map();

    constructor(
        private databaseService: DatabaseService
    ) {
        super();
    }

    ngOnInit() {
        this.databaseService.query().subscribe((res: HttpResponse<IDatabase[]>) => {
            this.databases = res.body;
        },
            (res: HttpErrorResponse) => {
                console.error(res.statusText);
                if (res.status === 0) {
                    this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                } else if (res.status == 500) {
                    this.showErrorMsg(res.error.error);
                } else {
                    this.showErrorMsg(res.statusText);
                }
            });
    }

    /**
     * Submits a given query to the given database to be executed on the server. The response is then relayed to the client.
     */
    submitQuery(databaseName: string, query: string) {
        this.databases.forEach(db => {
            if (db.name === databaseName) {
                this.databaseService.submitQuery(db.name, query).subscribe((res: HttpResponse<any>) => {
                    this.queryResponses.set(db.name, res.body);
                    this.showError = false;
                },
                    (res: HttpErrorResponse) => {
                        if (res.status === 0) {
                            this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                        } else if (res.status == 500) {
                            this.showErrorMsg(res.error.error);
                        } else {
                            this.showErrorMsg(res.statusText);
                        }
                    });
            }
            return;
        })
    }
}
