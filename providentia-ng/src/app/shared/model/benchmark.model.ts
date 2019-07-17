import { IDataset } from 'src/app/shared/model//dataset.model';
import { IDatabase } from 'src/app/shared/model//database.model';
import { IAnalysis } from 'src/app/shared/model//analysis.model';

export interface IBenchmark {
    id?: String;
    database?: IDatabase;
    dataset?: IDataset;
    analysis?: IAnalysis;
    date_executed?: Date;
    query_time?: Number;
    analysis_time?: Number;
    status?: String;
}

export class Benchmark implements IBenchmark {

    constructor(
        public id?: String,
        public database?: IDatabase,
        public dataset?: IDataset,
        public analysis?: IAnalysis,
        public date_executed?: Date,
        public query_time?: Number,
        public analysis_time?: Number,
        public status?: String
    ) {}

}
