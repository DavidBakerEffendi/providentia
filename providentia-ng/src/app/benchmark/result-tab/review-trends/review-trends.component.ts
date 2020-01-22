import { Component, OnInit, Input } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { ReviewTrendService, IReviewTrend, IBenchmark } from '../../../shared';

@Component({
    selector: 'prv-review-trends',
    templateUrl: './review-trends.component.html'
})
export class ReviewTrendsComponent implements OnInit {
    @Input()
    benchmark: IBenchmark;
    analysisResults: IReviewTrend[];

    public chartDatasets: Array<any> = [];

    public chartLabels: Array<any> = ['Length', 'Cool', 'Funny', 'Useful', 'Sentiment'];

    public chartColors: Array<any> = [
        { backgroundColor: 'rgba(252, 28, 7, .2)', borderColor: 'rgba(184, 18, 3, .7)' },
        { backgroundColor: 'rgba(254, 97, 233, .2)', borderColor: 'rgba(183, 0, 159, .7)' },
        { backgroundColor: 'rgba(71, 255, 86, .2)', borderColor: 'rgba(0, 158, 13, .7)' },
        { backgroundColor: 'rgba(255, 167, 45, .2)', borderColor: 'rgba(209, 121, 0, .7)' },
        { backgroundColor: 'rgba(255, 8, 94, .2)', borderColor: 'rgba(143, 0, 50, .7)' },
        { backgroundColor: 'rgba(4, 0, 255, .2)', borderColor: 'rgba(2, 0, 135, .7)' }
    ];

    public chartOptions: any = {
        responsive: true
    };
    public chartClicked(e: any): void { }
    public chartHovered(e: any): void { }

    constructor(
        private reviewTrendService: ReviewTrendService
    ) { }

    ngOnInit(): void {
        this.getAnalysisResults(this.benchmark);
    }

    /**
     * Get results from the given analysis in this benchmark.
     * @param benchmark the benchmark to look for results from.
     */
    getAnalysisResults(benchmark: IBenchmark) {
        if (benchmark.analysis.analysis_id === this.reviewTrendService.analysisId) {
            this.reviewTrendService.getResults(benchmark.benchmark_id.valueOf()).subscribe((res: HttpResponse<IReviewTrend[]>) => {
                // Sort by stars
                res.body.sort((a, b) => (a.stars > b.stars) ? 1 : -1);
                this.analysisResults = res.body;
                res.body.map((a: IReviewTrend, i: number) => {
                    const starData = [];
                    starData.push(res.body[i].length * 100.0);
                    starData.push(res.body[i].cool * 100.0);
                    starData.push(res.body[i].funny * 100.0);
                    starData.push(res.body[i].useful * 100.0);
                    starData.push(res.body[i].sentiment * 100.0);
                    this.chartDatasets.push({
                        data: starData,
                        label: `${res.body[i].stars} Star Reviews`
                    });
                });
            }, (res: HttpErrorResponse) => {
                console.error(res.message);
            });
        }
    }

}
