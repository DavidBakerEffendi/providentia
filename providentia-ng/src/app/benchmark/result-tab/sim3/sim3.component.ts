import { Component, OnInit,  Input } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Sim3Service, ISim3Result, IBenchmark } from '../../../shared';

@Component({
    selector: 'prv-sim3',
    templateUrl: './sim3.component.html',
})
export class Sim3ResultComponent implements OnInit {
    @Input()
    public benchmark: IBenchmark;
    public noResponses: number;

    constructor(
        public sim3Service: Sim3Service
    ) { }

    ngOnInit(): void {
        this.getAnalysisResults(this.benchmark);
    }

    /**
     * Get results from the given analysis in this benchmark.
     * @param benchmark the benchmark to look for results from.
     */
    getAnalysisResults(benchmark: IBenchmark) {
        if (benchmark.analysis.analysis_id === this.sim3Service.analysisId) {
            this.sim3Service.getResults(benchmark.benchmark_id.valueOf()).subscribe((res: HttpResponse<ISim3Result[]>) => {
                const simResult = res.body.pop();
                this.noResponses = simResult.no_responses;
            }, (res: HttpErrorResponse) => {
                console.error(res.message);
            });
        }
    }

}
