import { IDataset } from './dataset.model';

export interface IAnalysis {
    id?: String;
    dataset?: IDataset;
    name?: String;
    description?: String;
}

export class Analysis implements IAnalysis {

    constructor(
        public id?: String,
        public dataset?: IDataset,
        public name?: String,
        public description?: String
    ) {}

}
