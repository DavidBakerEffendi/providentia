import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IDatabase, DatabaseService, InfoMessage } from '../shared';
import APP_CONFIG from '../app.config';

@Component({
    selector: 'prv-databases',
    templateUrl: './databases.component.html',
    styleUrls: ['databases.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatabasesComponent extends InfoMessage implements OnInit {

    databases: IDatabase[];
    showSpinner: Array<boolean>;
    queryResponses = new Map();
    tigerGraphRest: string;

    constructor(
        private databaseService: DatabaseService,
        private ref: ChangeDetectorRef
    ) {
        super();
    }

    ngOnInit() {
        this.tigerGraphRest = APP_CONFIG.FLASK_API.substring(0, APP_CONFIG.FLASK_API.lastIndexOf(":") + 1) + "14240";
        this.showSpinner = [];
        this.databaseService.query().subscribe((res: HttpResponse<IDatabase[]>) => {
            this.databases = res.body;
            this.databases.forEach(() => this.showSpinner.push(false));
            this.ref.markForCheck();
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
                this.ref.markForCheck();
            });
    }

    /**
     * Submits a given query to the given database to be executed on the server. The response is then relayed to the client.
     */
    submitQuery(databaseName: string, query: string) {
        this.databases.forEach((db, i) => {
            if (db.name === databaseName) {
                this.showSpinner[i] = true;
                this.databaseService.submitQuery(db.name, query).subscribe((res: HttpResponse<any>) => {
                    this.queryResponses.set(db.name, res.body);
                    this.showSpinner[i] = false;
                    this.showError = false;
                    this.ref.markForCheck();
                },
                    (res: HttpErrorResponse) => {
                        this.showSpinner[i] = false;
                        if (res.status === 0) {
                            this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                        } else if (res.status == 500) {
                            this.showErrorMsg(res.error.error);
                        } else {
                            this.showErrorMsg(res.statusText);
                        }
                        this.ref.markForCheck();
                    });
                this.ref.markForCheck();
                return;
            }
        })
    }
}
