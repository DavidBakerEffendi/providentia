import { IBenchmark } from 'src/app/shared/model//benchmark.model';

export interface IKate {
    id?: string;
    benchmark?: IBenchmark;
    business?: string;
    sentiment_average?: number;
    star_average?: number;
    total_reviews?: number;
}

export class Kate implements IKate {

    constructor(
        public id?: string,
        public benchmark?: IBenchmark,
        public business?: string,
        public sentiment_average?: number,
        public star_average?: number,
        public total_reviews?: number
    ) {}

}
