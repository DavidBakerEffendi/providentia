export interface IDataset {
    id?: String;
    name?: String;
    description?: String;
    icon?: String;
}

export class Dataset implements IDataset {

    constructor(
        public id?: String,
        public name?: String,
        public description?: String,
        public icon?: String,
    ) {}

}
