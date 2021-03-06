import { Component, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';
import { InfoMessage, IBenchmark, BenchmarkService, IServerLog, LogService } from '../shared';
import { ActivatedRoute, Router } from '@angular/router';
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { ColourGenerator } from '../shared';

export class Stats {
    mean?: number;
    median?: number;
    max?: number;
    min?: number;
}

@Component({
    selector: 'prv-benchmark',
    templateUrl: './benchmark.component.html',
    styleUrls: ['benchmark.scss'],
    providers: [{
        provide: STEPPER_GLOBAL_OPTIONS, useValue: { displayDefaultIndicatorType: false }
    }]
})
export class BenchmarkComponent extends InfoMessage implements OnInit {

    benchmark: IBenchmark;
    id: string;

    cpuStats = new Stats();
    memoryStats = new Stats();

    public perfMemoryData: Array<any>;
    public perfCPUData: Array<any>;

    public chartColors: Array<any> = [{
        backgroundColor: ['#FF4081', '#455A64'],
        hoverBackgroundColor: ['#FF80AB', '#90A4AE'],
        borderWidth: 1,
    }];

    public perfCPUColors: Array<any> = [];

    public perfMemoryColors: Array<any> = [
        {
            backgroundColor: 'rgba(105, 0, 132, .2)',
            borderColor: 'rgba(200, 99, 132, .7)',
            borderWidth: 2,
        },
    ];

    public perfOptions: any = {
        responsive: true,
        spanGaps: true,
        scales: {
            xAxes: [{
                type: 'linear'
            }]
        }
    };

    constructor(
        private route: Router,
        private router: ActivatedRoute,
        private benchmarkService: BenchmarkService,
        private logService: LogService,
        private sanitizer: DomSanitizer
    ) {
        super();
        this.perfCPUColors.forEach(option => {
            option.fill = false;
            option.borderWidth = 2;
        });
        this.router.params.subscribe(params => this.id = params.id);
    }

    ngOnInit() {
        this.getResult(this.id);
    }

    /**
     * Obtains the information about this benchmark.
     * @param id the UUID of this benchmark.
     */
    getResult(id: string) {
        this.benchmarkService.find(id)
            .subscribe((res: HttpResponse<IBenchmark>) => {
                this.benchmark = res.body;
                // Get the performance of the server during the time executed
                this.getPerformance(new Date(this.benchmark.date_executed),
                    this.benchmark.query_time.valueOf() + this.benchmark.analysis_time.valueOf());
                // Authorize the BASE64 encoding of the icons
                this.sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64,' + this.benchmark.database.icon);
                this.sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64,' + this.benchmark.dataset.icon);
            },
                (res: HttpErrorResponse) => {
                    console.error(res.message);
                    if (res.status === 404) {
                        this.route.navigate(['/404']);
                    }
                }
            );
    }

    /**
     * Obtains the system logs during the time this analysis was run.
     * @param fromDate the from date.
     * @param duration the duration of the analysis.
     */
    getPerformance(fromDate: Date, duration: number) {
        const toDate = new Date(fromDate.getTime() + duration);
        this.logService.getFromTo(fromDate, toDate).subscribe((res: HttpResponse<IServerLog[]>) => {
            if (res.body.map) {
                const cpuLogs = res.body.map(a => a.cpu_logs);
                // Map CPU data
                this.perfCPUData = new Array<any>();
                cpuLogs.forEach((cpuLog, j) => {
                    // This will produce an array of arrays where each element shows the percentage of
                    // each core respectively e.g. [core1, core2] => [40.3, 39.4]
                    const cpuPercentages = cpuLog.map(a => a.cpu_perc);
                    // This loop will transform the rows into a columnwise form for the chart
                    for (let i = 0; i < cpuPercentages.length; i++) {
                        if (this.perfCPUData[i] === undefined || this.perfCPUData[i] === null) {
                            this.perfCPUData[i] = {
                                data: [],
                                label: `Core ${i}`
                            };
                        }
                        this.perfCPUData[i].data.push({ x : j, y : cpuPercentages[i] });
                    }
                    // Check colours
                    if (cpuPercentages.length > this.perfCPUColors.length) {
                        while (cpuPercentages.length > this.perfCPUColors.length) {
                            const newCol = ColourGenerator.getRandomColor();
                            this.perfCPUColors.push({
                                backgroundColor: ColourGenerator.adjust(newCol, 80),
                                borderColor: newCol,
                            });
                        }
                        this.perfCPUColors.forEach(option => {
                            option.fill = false;
                            option.borderWidth = 2;
                        });
                    }
                });
                this.setStats(this.perfCPUData, this.cpuStats);
                const memoryDataFlat = res.body.map(a => a.memory_perc);
                const perfMemoryD = [];
                // Map memory data
                memoryDataFlat.forEach((d, i) => {
                    perfMemoryD.push({ x : i, y : d });
                });
                this.perfMemoryData = [{
                    data: perfMemoryD,
                    label: 'Memory Percentage'
                }];
                this.setStats(this.perfMemoryData, this.memoryStats);
            }
        },
            (res: HttpErrorResponse) => {
                console.error(res.message);
                if (res.status === 503) {
                    this.showWarnMsg('No system information was logged during this analysis.');
                }
            });
    }

    /**
     * Using the given data, extract basic statistics from it.
     * @param data the data to analyse.
     * @param stats the stats object to set found information to.
     */
    setStats(data: Array<any>, stats: Stats) {
        if (data.length === 0) {
            stats.mean = 0;
            stats.max = 0;
            stats.min = 0;
        }
        let totalPercentage = 0;
        let totalEntries = 0;
        let max = Number.MIN_SAFE_INTEGER;
        let min = Number.MAX_SAFE_INTEGER;
        const flattened = [].concat.apply([], data.map(a => a.data)).sort();
        flattened.forEach((e: { y: number; }, i: number) => {
            // Mean
            totalPercentage += e.y;
            totalEntries++;
            // Min/Max
            if (e.y > max) { max = e.y; }
            if (e.y < min) { min = e.y; }
            // Median
            if (i === Math.floor(flattened.length / 2)) {
                if (flattened.length % 2 === 0 && i + 1 < flattened.length) {
                    stats.median = (flattened[i].y + flattened[i + 1].y) / 2;
                } else { stats.median = flattened[i].y; }
            }
        });
        // Finalize
        stats.mean = totalPercentage / totalEntries;
        stats.max = max;
        stats.min = min;
    }

}
