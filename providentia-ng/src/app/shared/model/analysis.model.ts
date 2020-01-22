import { IDataset } from './dataset.model';

export interface IAnalysis {
    analysis_id?: string;
    dataset?: IDataset;
    name?: string;
    description?: string;
}

export interface IAnalysisPerf {
    name?: string;
    avg?: number;
    stddev?: number;
}

export class Analysis implements IAnalysis {

    constructor(
        public analysis_id?: string,
        public dataset?: IDataset,
        public name?: string,
        public description?: string
    ) {}

}

export class AnalysisPerf implements IAnalysisPerf {

    constructor(
        public name?: string,
        public avg?: number,
        public stddev?: number
    ) {}

}
