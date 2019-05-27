import { IDataset } from 'src/app/shared/model//dataset.model';
import { IDatabase } from 'src/app/shared/model//database.model';

export interface IResult {
    id?: String;
    database?: IDatabase;
    dataset?: IDataset;
    date_executed?: Date;
    title?: String;
    description?: String;
    query_time?: Number;
    analysis_time?: Number;
}

export class Result implements IResult {

    constructor(
        public id?: String,
        public database?: IDatabase,
        public dataset?: IDataset,
        public date_executed?: Date,
        public title?: String,
        public description?: String,
        public query_time?: Number,
        public analysis_time?: Number
    ) {}

}
