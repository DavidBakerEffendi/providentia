import { IBenchmark } from 'src/app/shared/model//benchmark.model';

export interface IKate {
    id?: String;
    benchmark?: IBenchmark;
    business?: String;
    sentiment_average?: Number;
    star_average?: Number;
    total_reviews?: Number;
}

export class Kate implements IKate {

    constructor(
        public id?: String,
        public benchmark?: IBenchmark,
        public business?: String,
        public sentiment_average?: Number,
        public star_average?: Number,
        public total_reviews?: Number
    ) {}

}
