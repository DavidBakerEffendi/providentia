import { Component, OnInit,  Input } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { MatTableDataSource } from '@angular/material';
import { KateService, IKate, IBenchmark } from '../../../shared';

@Component({
    selector: 'prv-kate',
    templateUrl: './kate.component.html',
    styleUrls: ['kate.scss'],
})
export class KateResultComponent implements OnInit {
    @Input()
    benchmark: IBenchmark;

    analysisResults: MatTableDataSource<IKate>;
    displayedColumns: string[] = ['business', 'sentiment_average', 'star_average', 'total_reviews'];

    constructor(
        public kateService: KateService
    ) { }

    ngOnInit(): void {
        this.getAnalysisResults(this.benchmark);
    }

    /**
     * Get results from the given analysis in this benchmark.
     * @param benchmark the benchmark to look for results from.
     */
    getAnalysisResults(benchmark: IBenchmark) {
        if (benchmark.analysis.analysis_id === this.kateService.analysisId) {
            this.kateService.getResults(benchmark.benchmark_id.valueOf()).subscribe((res: HttpResponse<IKate[]>) => {
                this.analysisResults = new MatTableDataSource<IKate>(res.body);
            }, (res: HttpErrorResponse) => {
                console.error(res.message);
            });
        }
    }

}
