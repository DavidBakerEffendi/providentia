export interface IResult {
    id?: String;
    database?: String;
    dataset?: String;
    date_executed?: Date;
    title?: String;
    description?: String;
    query_time?: Number;
    analysis_time?: Number;
}

export class Result {

    constructor(
        public id?: String,
        public database?: String,
        public dataset?: String,
        public date_executed?: Date,
        public title?: String,
        public description?: String,
        public query_time?: Number,
        public analysis_time?: Number
    ) {}

}
