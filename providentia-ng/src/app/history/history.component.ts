import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { InfoMessage, BenchmarkService, IBenchmark } from '../shared';
import { PageEvent } from '@angular/material';

@Component({
    selector: 'prv-history',
    templateUrl: './history.component.html',
    styleUrls: ['history.scss']
})
export class HistoryComponent extends InfoMessage implements OnInit {

    protected displayedColumns: string[] = ['database', 'dataset', 'analysis', 'date_executed', 'query_time', 'analysis_time', 'status'];
    protected dataSource: MatTableDataSource<IBenchmark>;
    protected showSpinner: boolean;
    protected showPageSpinner: boolean;
    protected emptyResultSet: boolean;

    protected pageSize = 10;
    protected totalResults: number;
    protected pageSizeOptions: number[] = [10, 15, 25];

    constructor(
        private benchmarkService: BenchmarkService,
        private ref: ChangeDetectorRef
    ) { super(); }

    ngOnInit() {
        this.getPaginationInfo(0);
    }

    getPaginationInfo(page: number) {
        this.benchmarkService.total().subscribe((res: HttpResponse<any>) => {
            this.totalResults = res.body.total;
        })
        this.benchmarkService.paginate(this.pageSize, page)
            .subscribe((res: HttpResponse<IBenchmark[]>) => {
                this.dataSource = new MatTableDataSource<IBenchmark>(res.body);
                this.showPageSpinner = false;
                this.ref.markForCheck();
            }, (res: HttpErrorResponse) => {
                console.error(res.statusText)
                if (res.status === 0) {
                    this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                } else if (res.status == 500) {
                    this.showErrorMsg(res.error.error);
                } else if (res.status == 503) {
                    this.showInfoMsg(res.error.error);
                    this.emptyResultSet = true;
                } else {
                    this.showErrorMsg(res.statusText);
                }
                this.showPageSpinner = false;
                this.ref.markForCheck();
            });
    }

    handlePageEvent(e: PageEvent) {
        this.showPageSpinner = true;
        this.pageSize = e.pageSize
        this.getPaginationInfo(e.pageIndex);
    }

}
