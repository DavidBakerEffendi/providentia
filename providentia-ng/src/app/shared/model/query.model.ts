import { IDatabase } from 'src/app/shared/model//database.model';
import { IAnalysis } from 'src/app/shared/model//analysis.model';

export interface IQuery {
    query_id?: String;
    database?: IDatabase;
    analysis?: IAnalysis;
    query?: String;
    language?: String;
}

export class Query implements IQuery {

    constructor(
        public query_id?: String,
        public database?: IDatabase,
        public analysis?: IAnalysis,
        public query?: String,
        public language?: String
    ) {}

}
