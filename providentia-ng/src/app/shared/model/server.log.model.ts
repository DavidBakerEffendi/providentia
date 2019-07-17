import { ICPULog } from 'src/app/shared/model//cpu.log.model';

export interface IServerLog {
    log_id?: Number;
    captured_at?: Date;
    cpu_logs?: ICPULog[];
    memory_perc?: Number;
}

export class ServerLog implements IServerLog {

    constructor(
        public log_id?: Number,
        public captured_at?: Date,
        public cpu_logs?: ICPULog[],
        public memory_perc?: Number
    ) {}

}
