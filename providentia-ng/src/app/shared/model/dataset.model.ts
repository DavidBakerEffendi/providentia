export interface IDataset {
    dataset_id?: String;
    name?: String;
    description?: String;
    icon?: String;
}

export class Dataset implements IDataset {

    constructor(
        public dataset_id?: String,
        public name?: String,
        public description?: String,
        public icon?: String,
    ) {}

}
