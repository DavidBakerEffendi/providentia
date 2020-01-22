export interface IDataset {
    dataset_id?: string;
    name?: string;
    description?: string;
    icon?: string;
}

export class Dataset implements IDataset {

    constructor(
        public dataset_id?: string,
        public name?: string,
        public description?: string,
        public icon?: string,
    ) {}

}
