import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { InfoMessage, IAnalysis, AnalysisService, IAnalysisPerf } from '../shared';

@Component({
    selector: 'prv-performance',
    templateUrl: './performance.component.html',
    styleUrls: ['performance.scss']
})
export class PerformanceComponent extends InfoMessage implements OnInit {

    analysiss: IAnalysis[];
    showSpinner: Array<boolean>;
    perfResponses = new Map<string, Array<any>>();
    errResponses = new Map<string, string>();
    public perfDataColors: Array<any> = [
        {
            backgroundColor: 'rgba(102, 16, 242, .2)',
            borderColor: 'rgba(75, 0, 130, .7)',
            borderWidth: 2,
        },
        {
            backgroundColor: 'rgba(105, 0, 132, .2)',
            borderColor: 'rgba(200, 99, 132, .7)',
            borderWidth: 2,
        },
    ];
    public perfDataOptions: any = {
        responsive: true,
          scales: {
            xAxes: [{
              stacked: true
              }],
            yAxes: [
            {
              stacked: true
            }
          ]
        }
      };
    public perfDataLabels: Array<string> = [];

    constructor(
        private analysisService: AnalysisService,
        private ref: ChangeDetectorRef
    ) {
        super();
    }

    ngOnInit() {
        this.showSpinner = [];
        this.analysisService.query().subscribe((res: HttpResponse<IAnalysis[]>) => {
            this.analysiss = res.body;
            this.analysiss.forEach((an, i) => {
                this.showSpinner.push(false);
                this.getAnalysisPerf(an, i);
            });
            this.ref.markForCheck();
        },
            (res: HttpErrorResponse) => {
                // console.error(res.statusText);
                if (res.status === 0) {
                    this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                } else if (res.status === 500) {
                    this.showErrorMsg(res.error.error);
                } else if (res.status === 503) {
                    this.showInfoMsg('No performance results founds. Please run a job under \'New Job\'.');
                } else {
                    this.showErrorMsg(res.statusText);
                }
                this.ref.markForCheck();
            });
    }

    getAnalysisPerf(an: IAnalysis, i: number) {
        this.analysisService.performance(an.analysis_id.toString())
            .subscribe((res: HttpResponse<IAnalysisPerf[]>) => {
                const mean = [];
                const dev = [];
                const labels = [];
                res.body.forEach((anPerf: IAnalysisPerf) => {
                    mean.push(anPerf.avg - (anPerf.stddev / 2));
                    dev.push(anPerf.stddev);
                    labels.push(anPerf.name);
                });
                if (this.perfDataLabels.length === 0) {
                    this.perfDataLabels = labels;
                }
                const data = [{
                    data: mean,
                    label: 'Response times'
                }, {
                    data: dev,
                    label: 'Standard deviation'
                }];

                this.perfResponses.set(an.analysis_id.toString(), data);
            }, (err) => {
                if (err.status === 0) {
                    this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                } else if (err.status === 500) {
                    this.showErrorMsg(err.error.error);
                } else if (err.status === 503 || err.status === 404) {
                    this.errResponses.set(an.name, 'No analysis results found! Add jobs for this analysis under \'New Job\'.');
                } else {
                    this.showErrorMsg(err.statusText);
                }
                this.ref.markForCheck();
            });
    }

}
