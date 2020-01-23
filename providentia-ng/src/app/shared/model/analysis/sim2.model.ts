import { IBenchmark } from 'src/app/shared/model//benchmark.model';

export interface ISim2Result {
    id?: string;
    benchmark?: IBenchmark;
    p1?: number;
    p2?: number;
    p3?: number;
}

export class Sim2Result implements ISim2Result {

    constructor(
        public id?: string,
        public benchmark?: IBenchmark,
        public p1?: number,
        public p2?: number,
        public p3?: number
    ) {}

}
