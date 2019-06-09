import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IBenchmark, BenchmarkService } from '../shared';
import { InfoMessage } from '../shared';

@Component({
    selector: 'prv-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent extends InfoMessage implements OnInit {

    recentBenchmarks: IBenchmark[];
    flaskUpdate: number;
    springUpdate: number;

    errorMsgBenchmarks: string;
    infoMsgBenchmarks: string;

    constructor(
        private benchmarkService: BenchmarkService
    ) {
        super();
    }

    ngOnInit() {
        this.getRecentBenchmarks();
        this.getFlaskMetrics();
        this.getSpringMetrics();
    }

    getRecentBenchmarks() {
        this.benchmarkService.query()
            .subscribe((res: HttpResponse<IBenchmark[]>) => {
                this.recentBenchmarks = res.body;
                this.showSuccessMsg('');
                console.debug(this.recentBenchmarks);
            },
            (res: HttpErrorResponse) => {
                console.error(res.statusText)
                if (res.status === 0) {
                    this.showErrorMsg('Server did not reply to request. The server is most likely down or encountered an exception.');
                } else if (res.status == 500) {
                    this.showErrorMsg(res.error.error);
                } else if (res.status == 503) {
                    this.showInfoMsg(res.error.error);
                } else {
                    this.showErrorMsg(res.statusText);
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

    setTileClass(analyisTime, queryTime) {
        if (analyisTime ==-1 && queryTime == -1) {
            return "light-blue  accent-1"
        } else if (analyisTime == 0 && queryTime == 0) {
            return "orange accent-1"
        } else {
            return "indigo accent-1"
        }
    }
}
