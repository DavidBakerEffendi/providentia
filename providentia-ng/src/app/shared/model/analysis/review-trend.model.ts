import { IBenchmark } from 'src/app/shared/model//benchmark.model';

export interface IReviewTrend {
    id?: string;
    benchmark?: IBenchmark;
    stars?: number;
    length?: number;
    cool?: number;
    funny?: number;
    useful?: number;
    sentiment?: number;
}

export class ReviewTrend implements IReviewTrend {

    constructor(
        public id?: string,
        public benchmark?: IBenchmark,
        public stars?: number,
        public length?: number,
        public cool?: number,
        public funny?: number,
        public useful?: number,
        public sentiment?: number
    ) {}

}
