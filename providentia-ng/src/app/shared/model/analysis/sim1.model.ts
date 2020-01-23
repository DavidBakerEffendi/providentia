import { IBenchmark } from 'src/app/shared/model//benchmark.model';

export interface ISim1Result {
    id?: string;
    benchmark?: IBenchmark;
    avg_ttas?: number;
    avg_tth?: number;
}

export class Sim1Result implements ISim1Result {

    constructor(
        public id?: string,
        public benchmark?: IBenchmark,
        public avg_ttas?: number,
        public avg_tth?: number
    ) {}

}
