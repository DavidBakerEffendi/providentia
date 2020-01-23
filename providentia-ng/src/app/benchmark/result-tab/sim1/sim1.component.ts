import { Component, OnInit,  Input } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Sim1Service, ISim1Result, IBenchmark } from '../../../shared';

@Component({
    selector: 'prv-sim1',
    templateUrl: './sim1.component.html',
})
export class Sim1ResultComponent implements OnInit {
    @Input()
    public benchmark: IBenchmark;
    public avgTtas: number;
    public avgTth: number;

    constructor(
        public sim1Service: Sim1Service
    ) { }

    ngOnInit(): void {
        this.getAnalysisResults(this.benchmark);
    }

    /**
     * Get results from the given analysis in this benchmark.
     * @param benchmark the benchmark to look for results from.
     */
    getAnalysisResults(benchmark: IBenchmark) {
        if (benchmark.analysis.analysis_id === this.sim1Service.analysisId) {
            this.sim1Service.getResults(benchmark.benchmark_id.valueOf()).subscribe((res: HttpResponse<ISim1Result[]>) => {
                const simResult = res.body.pop();
                this.avgTtas = simResult.avg_ttas;
                this.avgTth = simResult.avg_tth;
            }, (res: HttpErrorResponse) => {
                console.error(res.message);
            });
        }
    }

}
