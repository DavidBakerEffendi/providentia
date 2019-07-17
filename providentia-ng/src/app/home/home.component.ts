import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { InfoMessage, IServerLog, LogService, IBenchmark, BenchmarkService } from '../shared';

@Component({
    selector: 'prv-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent extends InfoMessage implements OnInit, OnDestroy {

    recentBenchmarks: IBenchmark[];
    lastServerUpdate: number;
    serverPoll;

    errorMsgBenchmarks: string;
    infoMsgBenchmarks: string;

    public chartDatasets: Array<any>;

    public chartColors: Array<any> = [
        {
            backgroundColor: 'rgba(105, 0, 132, .2)',
            borderColor: 'rgba(200, 99, 132, .7)',
            borderWidth: 2,
        },
    ];

    public chartOptions: any = {
        responsive: true
    };

    constructor(
        private benchmarkService: BenchmarkService,
        private logService: LogService
    ) {
        super();
    }

    ngOnInit() {
        this.getRecentBenchmarks();
        this.getServerMetrics()
        this.serverPoll = setInterval(() => this.getServerMetrics(), 5000);
    }

    ngOnDestroy() {
        if (this.serverPoll) {
            clearInterval(this.serverPoll);
        }
    }

    getRecentBenchmarks() {
        this.benchmarkService.query(4)
            .subscribe((res: HttpResponse<IBenchmark[]>) => {
                this.recentBenchmarks = res.body;
                this.showError = false;
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

    /**
     * Get the latest server metrics.
     */
    getServerMetrics() {
        this.lastServerUpdate = Date.now();
        this.logService.getRecent().subscribe((res: HttpResponse<IServerLog[]>) => {
            // console.table(res.body);
            const logs = res.body;
            const memory = logs.map(a => a.memory_perc);
            console.log(memory);
            this.chartDatasets = [{data: memory, label: 'Memory Percentage'}];
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

    setTileClass(status) {
        if (status === 'PROCESSING') {
            return "light-blue  accent-1"
        } else if (status === 'WAITING') {
            return "orange accent-1"
        } else {
            return "indigo accent-1"
        }
    }
}
