import { Component, OnInit, Input } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IQuery, QueryService, Query } from '../../../shared';

@Component({
    selector: 'prv-query',
    templateUrl: './queries.component.html'
})
export class QueryResultComponent implements OnInit {

    @Input()
    private analysisId: string;
    @Input()
    private databaseId: string;

    public queries: Query[];

    constructor(
        private queryService: QueryService,
    ) { }

    ngOnInit(): void {
        this.getQueries();
    }

    /**
     * Retrieves the queries used for this analysis based on the database being queried.
     */
    getQueries() {
        this.queryService.getQueries(this.analysisId, this.databaseId).subscribe((res: HttpResponse<IQuery[]>) => {
            res.body.forEach((q: IQuery) => {
                q.query = q.query
                    .split('\\n').join('<br/>')
                    .split('\\t').join('&nbsp;&nbsp;');
            });
            this.queries = res.body;
        }, (res: HttpErrorResponse) => {
            console.error(res.message);
        });
    }

}
