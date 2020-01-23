import { Component, OnInit, Input } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Sim2Service, ISim2Result, IBenchmark } from '../../../shared';

@Component({
    selector: 'prv-sim2',
    templateUrl: './sim2.component.html',
})
export class Sim2ResultComponent implements OnInit {
    @Input()
    public benchmark: IBenchmark;
    public simResult: ISim2Result;

    constructor(
        public sim2Service: Sim2Service
    ) { }

    ngOnInit(): void {
        this.getAnalysisResults(this.benchmark);
    }

    /**
     * Get results from the given analysis in this benchmark.
     * @param benchmark the benchmark to look for results from.
     */
    getAnalysisResults(benchmark: IBenchmark) {
        if (benchmark.analysis.analysis_id === this.sim2Service.analysisId) {
            this.sim2Service.getResults(benchmark.benchmark_id.valueOf()).subscribe((res: HttpResponse<ISim2Result[]>) => {
                this.simResult = res.body.pop();
            }, (res: HttpErrorResponse) => {
                console.error(res.message);
            });
        }
    }

}
