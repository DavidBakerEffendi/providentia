import { IBenchmark } from 'src/app/shared/model//benchmark.model';

export interface ISim3Result {
    id?: string;
    benchmark?: IBenchmark;
    no_responses?: number;
}

export class Sim3Result implements ISim3Result {

    constructor(
        public id?: string,
        public benchmark?: IBenchmark,
        public no_responses?: number
    ) {}

}
