import { IDataset } from './dataset.model';

export interface IAnalysis {
    analysis_id?: String;
    dataset?: IDataset;
    name?: String;
    description?: String;
}

export class Analysis implements IAnalysis {

    constructor(
        public analysis_id?: String,
        public dataset?: IDataset,
        public name?: String,
        public description?: String
    ) {}

}
