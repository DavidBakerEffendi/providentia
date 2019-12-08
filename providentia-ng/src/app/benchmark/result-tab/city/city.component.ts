import { Component, OnInit, Input, ChangeDetectorRef } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { CitySentimentService, ICitySentiment, IBenchmark } from '../../../shared';

@Component({
    selector: 'prv-city-sentiment',
    templateUrl: './city.component.html'
})
export class CitySentimentComponent implements OnInit {
    @Input()
    benchmark: IBenchmark;
    analysisResult: ICitySentiment;

    public chartDatasets: Array<any> = [];

    public chartLabels: Array<any> = ['Stars', 'Sentiment'];

    public chartColors: Array<any> = [
        { backgroundColor: 'rgba(252, 28, 7, .2)', borderColor: 'rgba(184, 18, 3, .7)' },
        { backgroundColor: 'rgba(254, 97, 233, .2)', borderColor: 'rgba(183, 0, 159, .7)' },
        { backgroundColor: 'rgba(71, 255, 86, .2)', borderColor: 'rgba(0, 158, 13, .7)' },
        { backgroundColor: 'rgba(255, 167, 45, .2)', borderColor: 'rgba(209, 121, 0, .7)' },
        { backgroundColor: 'rgba(255, 8, 94, .2)', borderColor: 'rgba(143, 0, 50, .7)' },
        { backgroundColor: 'rgba(4, 0, 255, .2)', borderColor: 'rgba(2, 0, 135, .7)' }
    ];

    public chartOptions: any = {
        responsive: true,
        legend: {
            display: false
        },
        tooltips: {
            enabled: false
        },
        scales: {
            yAxes: [{
                gridLines: {
                    offsetGridLines: true
                }
            }]
        }
    };
    public chartClicked(e: any): void { }
    public chartHovered(e: any): void { }

    constructor(
        private citySentimentService: CitySentimentService,
        private ref: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.getAnalysisResults(this.benchmark);
    }

    /**
     * Get results from the given analysis in this benchmark.
     * @param benchmark the benchmark to look for results from.
     */
    getAnalysisResults(benchmark: IBenchmark) {
        if (benchmark.analysis.analysis_id === this.citySentimentService.analysisId) {
            this.citySentimentService.getResult(benchmark.benchmark_id.valueOf()).subscribe((res: HttpResponse<ICitySentiment>) => {
                this.analysisResult = res.body;
                this.chartDatasets.push({
                    data:[res.body.stars, res.body.sentiment]
                });
                this.ref.markForCheck();
            }, (res: HttpErrorResponse) => {
                console.error(res.message);
            });
        }
    }

}