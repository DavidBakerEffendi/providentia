import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IBenchmark, BenchmarkService, Benchmark } from '../shared';

@Component({
    selector: 'prv-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {

    recentBenchmarks: IBenchmark[];
    flaskUpdate: number;
    springUpdate: number;

    errorMsgBenchmarks: string;
    infoMsgBenchmarks: string;

    constructor(
        private benchmarkService: BenchmarkService
    ) { }

    ngOnInit() {
        this.getRecentBenchmarks();
        this.getFlaskMetrics();
        this.getSpringMetrics();
    }

    getRecentBenchmarks() {
        this.benchmarkService.query()
            .subscribe((res: HttpResponse<IBenchmark[]>) => {
                this.recentBenchmarks = res.body;
            },
            (res: HttpErrorResponse) => {
                console.error(res.statusText)
                if (res.status === 0) {
                    this.errorMsgBenchmarks = 'Server did not reply to request. The server is most likely down or encountered an exception.';
                } else if (res.status == 500) {
                    this.errorMsgBenchmarks = res.error.error;
                } else if (res.status == 503) {
                    this.infoMsgBenchmarks = res.error.error;
                } else {
                    this.errorMsgBenchmarks = res.statusText;
                }
                
            });
    }

    getFlaskMetrics() {
        // TODO
        this.flaskUpdate = Date.now();
    }

    getSpringMetrics() {
        // TODO
        this.springUpdate = Date.now();
    }
}
