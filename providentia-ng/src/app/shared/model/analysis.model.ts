import { IDataset } from './dataset.model';

export interface IAnalysis {
    analysis_id?: String;
    dataset?: IDataset;
    name?: String;
    description?: String;
}

export interface IAnalysisPerf {
    name?: String;
    avg?: number;
    stddev?: number;
}

export class Analysis implements IAnalysis {

    constructor(
        public analysis_id?: String,
        public dataset?: IDataset,
        public name?: String,
        public description?: String
    ) {}

}

export class AnalysisPerf implements IAnalysisPerf {

    constructor(
        public name?: String,
        public avg?: number,
        public stddev?: number
    ) {}

}
