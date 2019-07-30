import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { InfoMessage, BenchmarkService, IBenchmark } from '../shared';

@Component({
    selector: 'prv-history',
    templateUrl: './history.component.html',
    styleUrls: ['history.scss']
})
export class HistoryComponent extends InfoMessage implements OnInit {

    displayedColumns: string[] = ['database', 'dataset', 'analysis', 'date_executed', 'query_time', 'analysis_time', 'status'];
    dataSource: MatTableDataSource<IBenchmark>;

    constructor(
        private benchmarkService: BenchmarkService
    ) { super(); }

    ngOnInit() {
        this.getBenchmarks();
    }

    getBenchmarks() {
        this.benchmarkService.query()
            .subscribe((res: HttpResponse<IBenchmark[]>) => {
                this.dataSource = new MatTableDataSource<IBenchmark>(res.body);
            },
                (res: HttpErrorResponse) => {
                    console.error(res.statusText)
                    if (res.status === 0) {
                        this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                    } else if (res.status == 500) {
                        this.showErrorMsg(res.error.error);
                    } else if (res.status == 503) {
                        this.showInfoMsg(res.error.error);
                    } else {
                        this.showErrorMsg(res.statusText);
                    }

                });
    }
}
