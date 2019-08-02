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
    benchmarkPoll;

    errorMsgBenchmarks: string;
    infoMsgBenchmarks: string;

    public chartMemoryData: Array<any>;
    public chartCPUData: Array<any>;

    public chartCPUColors: Array<any> = [
        { backgroundColor: 'rgba(252, 28, 7, .2)', borderColor: 'rgba(184, 18, 3, .7)' },
        { backgroundColor: 'rgba(254, 97, 233, .2)', borderColor: 'rgba(183, 0, 159, .7)' },
        { backgroundColor: 'rgba(71, 255, 86, .2)', borderColor: 'rgba(0, 158, 13, .7)' },
        { backgroundColor: 'rgba(255, 167, 45, .2)', borderColor: 'rgba(209, 121, 0, .7)' },
        { backgroundColor: 'rgba(255, 8, 94, .2)', borderColor: 'rgba(143, 0, 50, .7)' },
        { backgroundColor: 'rgba(4, 0, 255, .2)', borderColor: 'rgba(2, 0, 135, .7)' }
    ];

    public chartMemoryColors: Array<any> = [
        {
            backgroundColor: 'rgba(105, 0, 132, .2)',
            borderColor: 'rgba(200, 99, 132, .7)',
            borderWidth: 2,
        },
    ];

    public chartOptions: any = {
        responsive: true,
        spanGaps: true,
        lineTension: 0,
        scales: {
            xAxes: [{
                type: 'linear'
            }]
        }
    };

    constructor(
        private benchmarkService: BenchmarkService,
        private logService: LogService
    ) {
        super();
        this.chartCPUColors.forEach(option => {
            option.fill = false;
            option.borderWidth = 2;
        })
    }

    ngOnInit() {
        this.getRecentBenchmarks();
        this.getServerMetrics()
        this.serverPoll = setInterval(() => this.getServerMetrics(), 2500);
        this.benchmarkPoll = setInterval(() => this.getRecentBenchmarks(), 5000);
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
            if (res.body.map) {
                const cpuLogs = res.body.map(a => a.cpu_logs)
                // Map CPU data
                this.chartCPUData = new Array<any>();
                cpuLogs.forEach((cpuLog, j) => {
                    // This will produce an array of arrays where each element shows the percentage of
                    // each core respectively e.g. [core1, core2] => [40.3, 39.4]
                    const cpuPercentages = cpuLog.map(a => a.cpu_perc);
                    // This loop will transform the rows into a columnwise form for the chart
                    for (let i = 0; i < cpuPercentages.length; i++) {
                        if (this.chartCPUData[i] === undefined || this.chartCPUData[i] === null) {
                            this.chartCPUData[i] = { data: [], label: `Core ${i}` };
                        }
                        this.chartCPUData[i].data.push({x:j, y:cpuPercentages[i]});
                    }
                });
                const memoryDataFlat = res.body.map(a => a.memory_perc);
                const perfMemoryD = []
                // Map memory data
                memoryDataFlat.forEach((d, i) => {
                    perfMemoryD.push({x: i, y:d})
                });
                // Map memory data
                this.chartMemoryData = [{ data: perfMemoryD, label: 'Memory Percentage' }];
            } else {
                this.showWarnMsg(res.body['message']);
            }
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

    setTileClass(status: string) {
        if (status === 'PROCESSING') {
            return "light-blue  accent-4"
        } else if (status === 'WAITING') {
            return "amber accent-4"
        } else {
            return "indigo accent-4"
        }
    }
}
