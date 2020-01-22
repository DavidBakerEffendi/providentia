import { IBenchmark } from 'src/app/shared/model//benchmark.model';

export interface ICitySentiment {
    id?: string;
    benchmark?: IBenchmark;
    stars?: number;
    sentiment?: number;
}

export class CitySentiment implements ICitySentiment {

    constructor(
        public id?: string,
        public benchmark?: IBenchmark,
        public stars?: number,
        public sentiment?: number
    ) {}

}
