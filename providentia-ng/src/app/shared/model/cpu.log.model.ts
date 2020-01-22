export interface ICPULog {
    log_id?: number;
    core_id?: number;
    system_log_id?: number;
    cpu_perc?: number;
}

export class CPULog implements ICPULog {

    constructor(
        public log_id?: number,
        public core_id?: number,
        public system_log_id?: number,
        public cpu_perc?: number
    ) {}

}
