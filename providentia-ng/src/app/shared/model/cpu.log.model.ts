export interface ICPULog {
    log_id?: Number;
    core_id?: Number;
    system_log_id?: Number;
    cpu_perc?: Number;
}

export class CPULog implements ICPULog {

    constructor(
        public log_id?: Number,
        public core_id?: Number,
        public system_log_id?: Number,
        public cpu_perc?: Number
    ) {}

}
