import { IDataset } from 'src/app/shared/model//dataset.model';
import { IDatabase } from 'src/app/shared/model//database.model';
import { IAnalysis } from 'src/app/shared/model//analysis.model';

export interface IBenchmark {
    benchmark_id?: string;
    database?: IDatabase;
    dataset?: IDataset;
    analysis?: IAnalysis;
    date_executed?: Date;
    query_time?: number;
    analysis_time?: number;
    status?: string;
}

export class Benchmark implements IBenchmark {

    constructor(
        public benchmark_id?: string,
        public database?: IDatabase,
        public dataset?: IDataset,
        public analysis?: IAnalysis,
        public date_executed?: Date,
        public query_time?: number,
        public analysis_time?: number,
        public status?: string
    ) {}

}
