import { IDatabase } from 'src/app/shared/model//database.model';
import { IAnalysis } from 'src/app/shared/model//analysis.model';

export interface IQuery {
    query_id?: string;
    database?: IDatabase;
    analysis?: IAnalysis;
    query?: string;
    language?: string;
}

export class Query implements IQuery {

    constructor(
        public query_id?: string,
        public database?: IDatabase,
        public analysis?: IAnalysis,
        public query?: string,
        public language?: string
    ) {}

}
